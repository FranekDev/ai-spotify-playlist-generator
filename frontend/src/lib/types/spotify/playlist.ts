export type ExternalUrls = {
    spotify: string;
  };
  
  export type Followers = {
    href: string | null;
    total: number;
  };
  
  export type Image = {
    url: string;
    height: number | null;
    width: number | null;
  };
  
  export type Owner = {
    id: string;
    display_name: string | null;
    external_urls: ExternalUrls;
    href: string;
    type: string;
    uri: string;
    followers: Followers | null;
  };
  
  export type Restrictions = {
    reason: string;
  };
  
  export type ExternalIds = {
    isrc: string | null;
    ean: string | null;
    upc: string | null;
  };
  
  export type Artist = {
    id: string;
    name: string;
    external_urls: ExternalUrls;
    href: string;
    type: string;
    uri: string;
  };
  
  export type Album = {
    id: string;
    name: string;
    images: Image[];
    album_type: string;
    total_tracks: number;
    release_date: string | null;
    release_date_precision: string | null;
    external_urls: ExternalUrls;
    href: string;
    type: string;
    uri: string;
    artists: Artist[] | null;
    available_markets: string[] | null;
    restrictions: Restrictions | null;
  };
  
  export type Track = {
    id: string;
    name: string;
    artists: Artist[];
    album: Album;
    duration_ms: number;
    explicit: boolean;
    external_urls: ExternalUrls;
    external_ids: ExternalIds | null;
    href: string;
    uri: string;
    type: string;
    preview_url: string | null;
    popularity: number;
    track_number: number;
    disc_number: number;
    is_playable: boolean | null;
    is_local: boolean;
  };
  
  export type User = {
    id: string;
    external_urls: ExternalUrls;
    href: string;
    type: string;
    uri: string;
    followers: Followers | null;
  };
  
  export type PlaylistTrack = {
    added_at: string;
    added_by: User;
    is_local: boolean;
    track: Track;
  };
  
  export type TracksInfo = {
    href: string;
    total: number;
    limit: number;
    offset: number;
    next: string | null;
    previous: string | null;
    items: PlaylistTrack[];
  };
  
  export type Playlist = {
    id: string;
    name: string;
    description: string | null;
    public: boolean;
    collaborative: boolean;
    owner: Owner;
    images: Image[];
    tracks: TracksInfo;
    external_urls: ExternalUrls;
    href: string;
    uri: string;
    snapshot_id: string;
    type: string;
    followers: Followers | null;
  };