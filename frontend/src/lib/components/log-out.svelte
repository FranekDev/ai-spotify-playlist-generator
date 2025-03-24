<script>
  import { LogOut } from "@lucide/svelte";

  import { Button } from "./ui/button";
  import { goto, invalidateAll } from "$app/navigation";

  const handleLogout = async () => {
    try {
      const response = await fetch("/api/logout", { method: "POST" });

      if (response.ok) {
        await invalidateAll();
        await goto("/");
      } else {
        console.error("Logout failed");
      }
    } catch (error) {
      console.error("Logout error:", error);
    }
  };
</script>

<Button variant="outline" onclick={() => handleLogout()} class="w-fit">
  <LogOut />
</Button>
