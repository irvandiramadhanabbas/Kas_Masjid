import { Router } from "express";
import {
  tampilkanKategori,
  tambahKategori,
  updateKategori,
  hapusKategori,
} from "../controllers/categoryController";
import { authMiddleware, requireRole } from "../middlewares/authMiddleware";

const router = Router();

router.use(authMiddleware);

// semua yang login boleh lihat daftar kategori
router.get("/", requireRole("KETUA", "BENDAHARA", "JAMAAH"), tampilkanKategori);

// hanya BENDAHARA yang boleh kelola
router.post("/", requireRole("BENDAHARA"), tambahKategori);
router.put("/:id", requireRole("BENDAHARA"), updateKategori);
router.delete("/:id", requireRole("BENDAHARA"), hapusKategori);

export default router;
