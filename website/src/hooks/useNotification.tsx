import { notificationsArraySchema } from "@/lib/schema";
import { useUserStore } from "@/lib/store";
import { getCookie } from "cookies-next";
import { useRouter } from "next/navigation";

export default function useNotification() {
  const { setNotifications } = useUserStore();
  const router = useRouter();

  const fetchNotificationsFromUser = async () => {
    try {
      const token = getCookie("token");
      if (!token) {
        router.push("/login");
        return;
      }

      const res = await fetch(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/notification/read-by-token`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );

      const data = await res.json();      

      const notificationRequestValidation =
        notificationsArraySchema.safeParse(data);
      if (notificationRequestValidation.error) return;

      setNotifications(notificationRequestValidation.data);
    } catch (error) {
      return;
    }
  };

  return { fetchNotificationsFromUser };
}
