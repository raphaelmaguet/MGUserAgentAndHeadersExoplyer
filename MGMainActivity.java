public DefaultExtractorsFactory defaultExtractorsFactory = new DefaultExtractorsFactory();
public DefaultHlsExtractorFactory defaultHlsExtractorFactory = new DefaultHlsExtractorFactory();
public DefaultHttpDataSource.Factory setDefaultHttpDataSourceFactory = new DefaultHttpDataSource.Factory();
public DataSource.Factory dataSourceFactory = () -> {
  HttpDataSource dataSource = setDefaultHttpDataSourceFactory.createDataSource();
  dataSource.setRequestProperty("HEADER_KEY", "HEADER_VALUE");
  return dataSource;
};
public PlayerView simpleExoPlayerView;
public SimpleExoPlayer player;
public TrackSelector trackSelector;

@Override
protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_main);
    
  setupPlayer();
  
}

public void setupPlayer() {
    if (player == null) {
      trackSelector = new DefaultTrackSelector(this);
      player = new SimpleExoPlayer.Builder(this)
              .setTrackSelector(trackSelector)
              .build();
      simpleExoPlayerView.setPlayer(player);
      simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
      simpleExoPlayerView.setUseController(false);
      
    }
}

public void setExoPlayer(String urlCH) {

        setDefaultHttpDataSourceFactory.setUserAgent("YOUR_USER-AGENT")
                .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS)
                .setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS)
                .setAllowCrossProtocolRedirects(true);

        defaultExtractorsFactory.setTsExtractorFlags(DefaultTsPayloadReaderFactory.FLAG_DETECT_ACCESS_UNITS);
        defaultExtractorsFactory.setTsExtractorFlags(DefaultTsPayloadReaderFactory.FLAG_ALLOW_NON_IDR_KEYFRAMES);

        MediaItem mediaItem1 = new MediaItem.Builder()
                .setUri(urlCH)
                .setMimeType(APPLICATION_M3U8)
                .build();

        // Create a HLS media source pointing to a playlist uri.
        HlsMediaSource hlsMediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                .setExtractorFactory(defaultHlsExtractorFactory)
                .setAllowChunklessPreparation(true)
                .createMediaSource(mediaItem1);

        // Set the media source to be played.
        player.setMediaSource(hlsMediaSource);
        // Prepare the player.
        player.prepare();
        player.setPlayWhenReady(true);
        player.addListener(new Player.Listener() {

                @Override
                public void onTracksChanged(@NotNull TrackGroupArray trackGroups, @NotNull TrackSelectionArray trackSelections) {
                    
                }

                @Override
                public void onMediaItemTransition(@Nullable @org.jetbrains.annotations.Nullable MediaItem mediaItem, int reason) {
                    assert mediaItem != null;
                    
                }

                @Override
                public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
                }

                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    if (isPlaying) {
                        MGPlayerStateLoading = true;
                        
                    } else {
                        MGPlayerStateLoading = false;
                    }
                }

                @Override
                public void onPlaybackStateChanged(int state) {
                    switch (state) {
                        case Player.STATE_READY:

                            progressBar.setVisibility(View.GONE);

                            break;
                        case Player.STATE_BUFFERING:
                            progressBar.setVisibility(View.VISIBLE);

                            break;
                        case Player.STATE_IDLE:
                            // This step is important, because it allows the player to restart reading the stream when you encounter a network error.
                            player.prepare();
                            player.setPlayWhenReady(true);

                            break;
                        case Player.STATE_ENDED:

                            break;
                    }
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {
                }

                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                }

                @Override
                public void onPlayerError(@NotNull ExoPlaybackException error) {
                    //Log.d(MGLogTag, "onPlayerError : " + error);

                    if (error.type == ExoPlaybackException.TYPE_SOURCE) {
                        IOException cause = error.getSourceException();
                        if (cause instanceof HttpDataSource.HttpDataSourceException) {
                            // An HTTP error occurred
                            HttpDataSource.HttpDataSourceException httpError = (HttpDataSource.HttpDataSourceException) cause;
                            // This is request for which the error occurred
                            DataSpec requestDataSpec = httpError.dataSpec;

                            // It's possible to find out more about the error both by casting and by
                            // querying the cause.
                            if (httpError instanceof HttpDataSource.InvalidResponseCodeException) {
                                // Cast to InvalidResponseCodeException and retrieve the response code,
                                // message and headers.
                            } else {
                                // Try calling httpError.getCause() to retrieve the underlying cause,
                                // although note that it may be null.
                            }
                        }
                    }

                }

                @Override
                public void onPlaybackParametersChanged(@NotNull PlaybackParameters playbackParameters) {
                }

            });

        }

    }
