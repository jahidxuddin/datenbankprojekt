import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { useUserStore } from "@/lib/store";

export default function BuyCreditsBanner() {
  const { credits } = useUserStore();

  return (
    <Card className="flex flex-col justify-between text-text">
      <CardHeader>
        <CardTitle className="font-bold">
          Guthaben:{" "}
          {credits === 0 ? "0,00" : credits.toString().replace(".", ",")}€
        </CardTitle>
      </CardHeader>
      <CardContent>
        Verwalten Sie Ihr Guthaben effizient und transparent über unser
        Dashboard, um Ihre Ausgaben im Blick zu behalten und Ihre Nutzung
        flexibel anzupassen.
      </CardContent>
      <CardFooter>
        <Button
          disabled
          className="w-full rounded-md bg-primary py-3 font-bold uppercase text-white hover:bg-blue-600"
        >
          Jetzt aufladen
        </Button>
      </CardFooter>
    </Card>
  );
}
