import Link from "next/link";
import { CarFront, CircleDollarSign, LayoutDashboard, Megaphone, Users } from "lucide-react";
import { ModeToggle } from "@/components/mode-toggle";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { cn } from "@/lib/utils";

const navItems = [
  { label: "Dashboard", icon: LayoutDashboard, href: "/" },
  { label: "Inventory", icon: CarFront, href: "#" },
  { label: "Customers", icon: Users, href: "#" },
  { label: "Sales", icon: CircleDollarSign, href: "#" },
  { label: "Promotions", icon: Megaphone, href: "#" },
];

type AppShellProps = {
  children: React.ReactNode;
};

export function AppShell({ children }: AppShellProps) {
  return (
    <div className="min-h-screen bg-background">
      <div className="grid min-h-screen grid-cols-1 lg:grid-cols-[240px_1fr]">
        <aside className="hidden border-r bg-card lg:block">
          <div className="flex h-16 items-center border-b px-5">
            <div className="flex items-center gap-2">
              <div className="rounded-md bg-primary p-1.5 text-primary-foreground">
                <CarFront className="size-4" />
              </div>
              <div>
                <p className="text-sm font-semibold">VeloxAuto</p>
                <p className="text-xs text-muted-foreground">Dealer Portal</p>
              </div>
            </div>
          </div>
          <nav className="space-y-1 p-3">
            {navItems.map((item) => (
              <Link
                key={item.label}
                href={item.href}
                className={cn(
                  "flex items-center gap-2 rounded-md px-3 py-2 text-sm text-muted-foreground transition-colors hover:bg-muted hover:text-foreground",
                  item.label === "Dashboard" && "bg-muted text-foreground"
                )}
              >
                <item.icon className="size-4" />
                {item.label}
              </Link>
            ))}
          </nav>
        </aside>

        <div className="flex min-h-screen flex-col">
          <header className="sticky top-0 z-10 border-b bg-card/95 backdrop-blur">
            <div className="flex h-16 items-center gap-3 px-4 md:px-6">
              <Input placeholder="Search VIN, make, model..." className="max-w-md" />
              <div className="ml-auto flex items-center gap-2">
                <Badge variant="secondary" className="hidden sm:inline-flex">
                  840 coins
                </Badge>
                <ModeToggle />
              </div>
            </div>
          </header>
          <div className="flex-1">{children}</div>
        </div>
      </div>
    </div>
  );
}
