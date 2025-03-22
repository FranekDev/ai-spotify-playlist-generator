export type ExternalUrls = {
    spotify: string;
  };
  
  export type Followers = {
    href: string;
    total: number;
  };
  
  export type Image = {
    url: string;
    height: number;
    width: number;
  };
  
  export type Owner = {
    external_urls: ExternalUrls;
    followers: Followers;
    href: string;
    id: string;
    type: string;
    uri: string;
    display_name: string;
  };
  
  export type Restrictions = {
    reason: string;
  };
  
  export type Artist = {
    external_urls: ExternalUrls;
    href: string;
    id: string;
    name: string;
    type: string;
    uri: string;
  };
  
  export type Album = {
    album_type: string;
    total_tracks: number;
    available_markets: string[];
    external_urls: ExternalUrls;
    href: string;
    id: string;
    images: Image[];
    name: string;
    release_date: string;
    release_date_precision: string;
    restrictions: Restrictions;
    type: string;
    uri: string;
    artists: Artist[];
  };
  
  export type ExternalIds = {
    isrc: string;
    ean: string;
    upc: string;
  };
  
  export type Track = {
    album: Album;
    artists: Artist[];
    available_markets: string[];
    disc_number: number;
    duration_ms: number;
    explicit: boolean;
    external_ids: ExternalIds;
    external_urls: ExternalUrls;
    href: string;
    id: string;
    is_playable: boolean;
    linked_from: Record<string, unknown>;
    restrictions: Restrictions;
    name: string;
    popularity: number;
    preview_url: string;
    track_number: number;
    type: string;
    uri: string;
    is_local: boolean;
  };
  
  export type AddedBy = {
    external_urls: ExternalUrls;
    followers: Followers;
    href: string;
    id: string;
    type: string;
    uri: string;
  };
  
  export type Item = {
    added_at: string;
    added_by: AddedBy;
    is_local: boolean;
    track: Track;
  };
  
  export type Tracks = {
    href: string;
    limit: number;
    next: string;
    offset: number;
    previous: string;
    total: number;
    items: Item[];
  };
  
  export type SpotifyCreatePlaylist = {
    collaborative: boolean;
    description: string;
    external_urls: ExternalUrls;
    followers: Followers;
    href: string;
    id: string;
    images: Image[];
    name: string;
    owner: Owner;
    public: boolean;
    snapshot_id: string;
    tracks: Tracks;
    type: string;
    uri: string;
  };