import { Router } from "express";
import {
  tampilkanTransaksi,
  tambahTransaksi,
  updateTransaksi,
  //hapusTransaction,
} from "../controllers/transactionController";
import { requireRole } from "../middlewares/authMiddleware";

const router = Router();

// semua yang login boleh lihat transaksi
router.get("/", tampilkanTransaksi);

// hanya BENDAHARA yang boleh input/edit/hapus transaksi
router.post("/", requireRole("BENDAHARA"), tambahTransaksi);
router.put("/:id", requireRole("BENDAHARA"), updateTransaksi);
//router.delete("/:id", requireRole("BENDAHARA"), hapusTransaction);

export default router;
