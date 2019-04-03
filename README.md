#Widgets library

## [Banner](Banner.java)

> A banner displays a prominent message and related optional actions.

### Usage
Add to your `layout.xml`:
```
<com.vonavilab.lib.widget.material.Banner
        android:id="@+id/banner"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:visibility="gone"
        app:buttonLeftText="Dismiss"
        app:buttonRightText="Turn on wifi"
        app:icon="@drawable/ic_signal_wifi_off_40dp"
        app:iconTint="@color/colorPrimary"
        app:messageText="You have lost connection to the Internet." />
```

then in your Activity/Fragment:
```
Banner banner = findViewById(R.id.banner);
banner.show();

// and later on
banner.dismiss();
```

Or create a banner from the code:
```
Banner banner = new Banner.Builder(context).setParent(rootView)
    .setIcon(R.drawable.ic_signal_wifi_off_40dp)
    .setMessage("You have lost connection to the Internet. This app is offline.")
    .setLeftButton("Dismiss", new BannerInterface.OnClickListener() {
        @Override
        public void onClick(BannerInterface banner) {
            banner.dismiss();
        }
    })
    .setRightButton("Turn on wifi", new BannerInterface.OnClickListener() {
        @Override
        public void onClick(BannerInterface banner) {
            // do something
        }
    })
    .setOnDismissListener(new BannerInterface.OnDismissListener() {
        @Override
        public void onDismiss() {
            // do something
        }
    })
    .setOnShowListener(new BannerInterface.OnShowListener() {
        @Override
        public void onShow() {
            // do something
        }
    })
    .create();
    ...
    banner.show();
```

### Styling

