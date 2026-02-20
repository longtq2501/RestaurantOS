import type { NextRequest } from "next/server";
import { NextResponse } from "next/server";

export function proxy(request: NextRequest) {
  // const token = request.cookies.get("restaurantos_access_token")?.value;
  // const isAuthPage = request.nextUrl.pathname.startsWith("/login") || 
  //                    request.nextUrl.pathname.startsWith("/register");

  return NextResponse.next();
}

export const proxyConfig = {
  matcher: ["/((?!api|_next/static|_next/image|favicon.ico).*)"],
};
