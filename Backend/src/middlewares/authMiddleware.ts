import jwt from "jsonwebtoken";
import { Request, Response, NextFunction } from "express";

console.log("🔥 LOADED authMiddleware.ts (THIS FILE) 🔥");

export type RoleName = "KETUA" | "BENDAHARA" | "JAMAAH";

export interface AuthUser {
  id: number;
  username: string;
  role: RoleName;
}

declare global {
  namespace Express {
    interface Request {
      user?: AuthUser;
    }
  }
}

export function authMiddleware(req: Request, res: Response, next: NextFunction) {
  console.log("AUTH HIT:", req.method, req.originalUrl);

  const authHeader = req.headers.authorization;
  const token = authHeader?.startsWith("Bearer ")
    ? authHeader.split(" ")[1]
    : undefined;

  if (!token) {
    console.log("AUTH FAIL: no token");
    return res.status(401).json({ message: "Unauthorized" });
  }

  try {
    const secret = (process.env.JWT_SECRET || "secret") as string;
    const decoded = jwt.verify(token, secret) as AuthUser;
    req.user = decoded;

    console.log("AUTH OK ✅ user:", decoded.id, decoded.role);
    next();
  } catch (e) {
    console.log("AUTH FAIL: invalid token");
    return res.status(401).json({ message: "Invalid token" });
  }
}

// middleware tambahan untuk batasi role
export function requireRole(...roles: RoleName[]) {
  return (req: Request, res: Response, next: NextFunction) => {
    if (!req.user || !roles.includes(req.user.role)) {
      return res.status(403).json({ message: "Forbidden" });
    }
    next();
  };
}
