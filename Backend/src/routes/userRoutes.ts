import { Router } from "express";
import {
  tampilkanPengguna,
  tambahPengguna,
  updatePengguna,
  resetpasswordPengguna,
  hapusPengguna,
} from "../controllers/userController";

const router = Router();

router.get("/", tampilkanPengguna);
router.post("/", tambahPengguna);
router.put("/:id", updatePengguna);
router.patch("/:id/reset-password", resetpasswordPengguna);
router.delete("/:id", hapusPengguna);

export default router;
