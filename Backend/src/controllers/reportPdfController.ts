import { Request, Response } from "express";
import PDFDocument from "pdfkit";
import { db } from "../db";

export async function eksporpdf(req: Request, res: Response) {
  const { startDate, endDate } = req.query as {
    startDate?: string;
    endDate?: string;
  };

  if (!startDate || !endDate) {
    return res.status(400).json({
      message: "startDate dan endDate wajib diisi (format YYYY-MM-DD)",
    });
  }
  if (startDate > endDate) {
    return res.status(400).json({ message: "startDate tidak boleh lebih besar dari endDate" });
  }

  try {
    const [summaryRows]: any = await db.query(
      `
      SELECT
        COALESCE(SUM(CASE WHEN UPPER(jenis) = 'PEMASUKAN' THEN nominal ELSE 0 END), 0) AS totalPemasukan,
        COALESCE(SUM(CASE WHEN UPPER(jenis) = 'PENGELUARAN' THEN nominal ELSE 0 END), 0) AS totalPengeluaran
      FROM transaksi
      WHERE tglTransaksi >= ? AND tglTransaksi <= ?
      `,
      [startDate, endDate]
    );

    const totalPemasukan = Number(summaryRows[0].totalPemasukan) || 0;
    const totalPengeluaran = Number(summaryRows[0].totalPengeluaran) || 0;
    const saldoPeriode = totalPemasukan - totalPengeluaran;

    const [trxRows]: any = await db.query(
      `
      SELECT
        t.id,
        t.tglTransaksi,
        t.jenis,
        t.nominal,
        t.keterangan,
        k.nama AS kategori_nama,
        u.username AS dicatat_oleh
      FROM transaksi t
      JOIN kategori k ON t.kategori_id = k.id
      JOIN pengguna u ON t.pengguna_id = u.id
      WHERE t.tglTransaksi >= ? AND t.tglTransaksi <= ?
      ORDER BY t.tglTransaksi ASC, t.id ASC
      `,
      [startDate, endDate]
    );

    // ========== 3. Buat PDF ==========
    const doc = new PDFDocument({
      size: "A4",
      margin: 40,
      // layout: "landscape" // kalau mau lebih lebar
    });

    res.setHeader("Content-Type", "application/pdf");
    res.setHeader(
      "Content-Disposition",
      `attachment; filename="laporan_kas_${startDate}_sd_${endDate}.pdf"`
    );
    doc.pipe(res);

    // ===== Helpers =====
    const fmtRupiah = (n: number) => `Rp ${Number(n || 0).toLocaleString("id-ID")}`;

    const page = () => ({
      left: doc.page.margins.left,
      right: doc.page.width - doc.page.margins.right,
      top: doc.page.margins.top,
      bottom: doc.page.height - doc.page.margins.bottom,
      width: doc.page.width - doc.page.margins.left - doc.page.margins.right,
    });

    const ensureSpace = (neededHeight: number, drawTableHeader?: () => void) => {
      const { bottom } = page();
      if (doc.y + neededHeight > bottom) {
        doc.addPage();
        if (drawTableHeader) drawTableHeader();
      }
    };

    // ===== Header dokumen =====
    doc.font("Helvetica-Bold").fontSize(16).text("LAPORAN KAS MASJID", { align: "center" });
    doc.moveDown(0.3);
    doc.font("Helvetica").fontSize(11).fillColor("#333333")
      .text(`Periode: ${startDate} s/d ${endDate}`, { align: "center" });
    doc.moveDown(1);

    // ===== Box Ringkasan =====
    const { left, right, width } = page();
    const boxX = left;
    const boxW = width;
    const boxY = doc.y;
    const boxPad = 10;
    const boxH = 70;

    doc.save();
    doc.roundedRect(boxX, boxY, boxW, boxH, 8).fillAndStroke("#F7F7F7", "#D0D0D0");
    doc.restore();

    doc.font("Helvetica-Bold").fontSize(12).fillColor("#111111")
      .text("Ringkasan", boxX + boxPad, boxY + boxPad);

    doc.font("Helvetica").fontSize(11).fillColor("#111111");
    const lineY = boxY + boxPad + 20;

    doc.text(`Total Pemasukan   : ${fmtRupiah(totalPemasukan)}`, boxX + boxPad, lineY);
    doc.text(`Total Pengeluaran : ${fmtRupiah(totalPengeluaran)}`, boxX + boxPad, lineY + 16);
    doc.text(`Saldo Periode     : ${fmtRupiah(saldoPeriode)}`, boxX + boxPad, lineY + 32);

    doc.y = boxY + boxH + 16;

    // ===== Judul tabel =====
    doc.font("Helvetica-Bold").fontSize(12).fillColor("#111111")
      .text("Daftar Transaksi");
    doc.moveDown(0.5);

    // ===== Table config =====
    const col = {
      tanggal: 80,
      jenis: 85,
      kategori: 120,
      nominal: 90,
      dicatat: 90,
      ket: 0, // sisanya
    };

    // hitung sisa untuk keterangan
    const tableX = left;
    const tableW = width;
    col.ket = tableW - (col.tanggal + col.jenis + col.kategori + col.nominal + col.dicatat);

    const rowPadY = 6;
    const rowPadX = 6;

    const drawRowBorders = (x: number, y: number, w: number, h: number) => {
      doc.save();
      doc.lineWidth(0.5).strokeColor("#E0E0E0");
      doc.rect(x, y, w, h).stroke();
      doc.restore();
    };

    const drawTableHeader = () => {
      const headerH = 26;
      ensureSpace(headerH);

      doc.save();
      doc.fillColor("#EFEFEF");
      doc.rect(tableX, doc.y, tableW, headerH).fill();
      doc.restore();

      doc.font("Helvetica-Bold").fontSize(10).fillColor("#111111");

      let x = tableX;
      const y = doc.y + 7;

      doc.text("Tanggal", x + rowPadX, y, { width: col.tanggal - rowPadX * 2 }); x += col.tanggal;
      doc.text("Jenis", x + rowPadX, y, { width: col.jenis - rowPadX * 2 }); x += col.jenis;
      doc.text("Kategori", x + rowPadX, y, { width: col.kategori - rowPadX * 2 }); x += col.kategori;
      doc.text("Nominal", x + rowPadX, y, { width: col.nominal - rowPadX * 2, align: "right" }); x += col.nominal;
      doc.text("Dicatat", x + rowPadX, y, { width: col.dicatat - rowPadX * 2 }); x += col.dicatat;
      doc.text("Keterangan", x + rowPadX, y, { width: col.ket - rowPadX * 2 });

      // border bawah header
      doc.save();
      doc.strokeColor("#D0D0D0").lineWidth(1);
      doc.moveTo(tableX, doc.y + headerH).lineTo(tableX + tableW, doc.y + headerH).stroke();
      doc.restore();

      doc.y += headerH;
    };

    const drawTableRow = (r: any) => {
      doc.font("Helvetica").fontSize(9).fillColor("#111111");

      const tanggal = r.tglTransaksi ?? "-";
      const jenis = (r.jenis ?? "-").toString().toUpperCase();
      const kategori = r.kategori_nama ?? "-";
      const nominal = fmtRupiah(Number(r.nominal || 0));
      const dicatat = r.dicatat_oleh ?? "-";
      const ket = r.keterangan?.toString().trim() ? r.keterangan : "-";

      // hitung tinggi row dari kolom yang paling tinggi (kategori & ket biasanya wrap)
      const hTanggal = doc.heightOfString(tanggal, { width: col.tanggal - rowPadX * 2 });
      const hJenis = doc.heightOfString(jenis, { width: col.jenis - rowPadX * 2 });
      const hKategori = doc.heightOfString(kategori, { width: col.kategori - rowPadX * 2 });
      const hNominal = doc.heightOfString(nominal, { width: col.nominal - rowPadX * 2 });
      const hDicatat = doc.heightOfString(dicatat, { width: col.dicatat - rowPadX * 2 });
      const hKet = doc.heightOfString(ket, { width: col.ket - rowPadX * 2 });

      const contentH = Math.max(hTanggal, hJenis, hKategori, hNominal, hDicatat, hKet);
      const rowH = contentH + rowPadY * 2;

      ensureSpace(rowH, drawTableHeader);

      const y = doc.y;
      drawRowBorders(tableX, y, tableW, rowH);

      let x = tableX;

      doc.text(tanggal, x + rowPadX, y + rowPadY, { width: col.tanggal - rowPadX * 2 }); x += col.tanggal;
      doc.text(jenis, x + rowPadX, y + rowPadY, { width: col.jenis - rowPadX * 2 }); x += col.jenis;
      doc.text(kategori, x + rowPadX, y + rowPadY, { width: col.kategori - rowPadX * 2 }); x += col.kategori;
      doc.text(nominal, x + rowPadX, y + rowPadY, { width: col.nominal - rowPadX * 2, align: "right" }); x += col.nominal;
      doc.text(dicatat, x + rowPadX, y + rowPadY, { width: col.dicatat - rowPadX * 2 }); x += col.dicatat;
      doc.text(ket, x + rowPadX, y + rowPadY, { width: col.ket - rowPadX * 2 });

      doc.y += rowH;
    };

    // ===== Render table =====
    drawTableHeader();

    if (!trxRows || trxRows.length === 0) {
      ensureSpace(30);
      doc.font("Helvetica").fontSize(10).fillColor("#444444")
        .text("Tidak ada transaksi pada periode ini.", tableX, doc.y + 8);
    } else {
      trxRows.forEach((row: any) => drawTableRow(row));
    }

    // ===== Footer (opsional) =====
    doc.moveDown(1);
    doc.fontSize(9).fillColor("#666666")
      .text(`Dicetak pada: ${new Date().toLocaleString("id-ID")}`, { align: "right" });

    doc.end();
  } catch (err) {
    console.error("getReportPdf error:", err);
    return res.status(500).json({ message: "Server error" });
  }
}
