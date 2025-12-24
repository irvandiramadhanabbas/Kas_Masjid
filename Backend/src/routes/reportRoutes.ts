import { Router } from "express";
import { tampilkanLaporan, } from "../controllers/reportController";
import { eksporpdf } from "../controllers/reportPdfController"; 
const router = Router();

// GET /reports?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD
router.get("/", tampilkanLaporan);

router.get("/pdf", eksporpdf);

export default router;
