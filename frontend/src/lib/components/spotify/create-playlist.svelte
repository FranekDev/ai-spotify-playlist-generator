<script lang="ts">
  import {
    createPlaylistSchema,
    type CreatePlaylistSchema,
  } from "$lib/types/spotify/schemas/create-playlist-schema";
  import * as Form from "$lib/components/ui/form";
  import { zodClient } from "sveltekit-superforms/adapters";
  import { Textarea } from "../ui/textarea";
  import {
    superForm,
    type Infer,
    type SuperValidated,
  } from "sveltekit-superforms";
  import Switch from "../ui/switch/switch.svelte";
  import { Input } from "../ui/input";
  import { enhance } from "$app/forms";
  import type { ApiError } from "$lib/types/api/api-error";
  import { toast } from "svelte-sonner";
  import Button from "../ui/button/button.svelte";
  import { LoaderCircle } from "@lucide/svelte";

  type Data = {
    data: SuperValidated<Infer<CreatePlaylistSchema>>;
  };
  let { data }: Data = $props();
  const form = superForm(data, {
    validators: zodClient(createPlaylistSchema),
  });

  const { form: formData } = form;

  const placeholder = "Tell me about your favorite music, what do you like?";
  let submit = $state(false);
</script>

<form
  method="POST"
  class="space-y-6"
  action="?/createPlaylist"
  use:enhance={() => {
    submit = true;
    return async ({ result, update }) => {
      submit = false;
      if (result.type === "failure") {
        const formData = result.data;

        if (formData && "apiError" in formData) {
          const apiError = formData.apiError as ApiError;
          toast.error(apiError.message);
        }
      } else if (result.type === "success") {
        toast.success("Playlist created successfully!");
      }

      update({ reset: false });
    };
  }}
>
  <div class="space-y-4">
    <Form.Field {form} name="name">
      <Form.Control let:attrs>
        <Form.Label class="text-sm font-medium text-zinc-700"
          >Playlist Name</Form.Label
        >
        <Input
          {...attrs}
          bind:value={$formData.name}
          class="mt-1 w-full rounded-md border border-zinc-300 px-4 py-2 focus:border-spotify focus:ring focus:ring-lime-200 focus:ring-opacity-50"
          placeholder="My Awesome Playlist"
          disabled={submit}
        />
        <Form.FieldErrors class="text-xs text-red-500 mt-1" />
      </Form.Control>
    </Form.Field>

    <Form.Field {form} name="description">
      <Form.Control let:attrs>
        <Form.Label class="text-sm font-medium text-zinc-700"
          >What playlist should We create for You?</Form.Label
        >
        <Textarea
          {...attrs}
          rows={5}
          {placeholder}
          bind:value={$formData.description}
          disabled={submit}
          class="mt-1 w-full rounded-md border border-zinc-300 px-4 py-2 focus:border-spotify focus:ring focus:ring-lime-200 focus:ring-opacity-50"
        />
        <Form.FieldErrors class="text-xs text-red-500 mt-1" />
      </Form.Control>
    </Form.Field>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <Form.Field {form} name="tracksAmount">
        <Form.Control let:attrs>
          <div class="space-y-1">
            <Form.Label class="text-sm font-medium text-zinc-700"
              >Tracks Amount</Form.Label
            >
            <div class="flex items-center">
              <Input
                {...attrs}
                type="number"
                bind:value={$formData.tracksAmount}
                min={1}
                max={100}
                disabled={submit}
                class="w-full rounded-md border border-zinc-300 px-4 py-2 focus:border-spotify focus:ring focus:ring-lime-200 focus:ring-opacity-50"
              />
            </div>
            <Form.FieldErrors class="text-xs text-red-500" />
          </div>
        </Form.Control>
      </Form.Field>

      <Form.Field {form} name="public">
        <Form.Control let:attrs>
          <div class="flex items-center justify-end h-full pt-6 gap-4">
            <Form.Label class="text-sm font-medium text-zinc-700"
              >Make Public</Form.Label
            >
            <Switch
              {...attrs}
              bind:checked={$formData.public}
              aria-label="Make playlist public"
              class="data-[state=checked]:bg-spotify"
              disabled={submit}
            />
          </div>
          <Form.FieldErrors class="text-xs text-red-500" />
        </Form.Control>
      </Form.Field>
    </div>
  </div>

  <div class="flex justify-end">
    <div class="w-fit min-w-[180px]">
      {#if submit}
        <Button
          class="w-full rounded-md bg-spotify px-6 py-2 text-sm font-medium text-zinc-900 hover:bg-spotify-dark focus:outline-none focus:ring-2 focus:ring-spotify-dark focus:ring-offset-2 transition-colors"
          disabled={true}
        >
          <LoaderCircle class="mr-2 h-4 w-4 animate-spin" />
          Creating...
        </Button>
      {:else}
        <Form.Button
          class="w-full rounded-md bg-spotify px-6 py-2 text-sm font-medium text-zinc-900 hover:bg-spotify-dark focus:outline-none focus:ring-2 focus:ring-spotify-dark focus:ring-offset-2 transition-colors"
          disabled={submit}
        >
          {submit ? "Creating..." : "Generate Playlist"}
        </Form.Button>
      {/if}
    </div>
  </div>
</form>
