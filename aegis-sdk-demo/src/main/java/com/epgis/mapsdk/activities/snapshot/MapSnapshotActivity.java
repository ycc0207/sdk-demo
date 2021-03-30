package com.epgis.mapsdk.activities.snapshot;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.epgis.epgisapp.R;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.geometry.LatLngBounds;
import com.epgis.mapsdk.log.Timber;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.OnMapReadyCallback;
import com.epgis.mapsdk.maps.Style;
import com.epgis.mapsdk.snapshotter.MapSnapshotter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by huangsiwen on 2019/3/25.
 */

public class MapSnapshotActivity extends AppCompatActivity {

    private GridLayout grid;
    private List<MapSnapshotter> snapshotters = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapsdk_activity_snapshotter);

        grid = findViewById(R.id.snapshot_grid);
        grid.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        grid.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        addSnapshots();
                    }
                });
    }

    private void addSnapshots() {
        for (int row = 0; row < grid.getRowCount(); row++) {
            for (int column = 0; column < grid.getColumnCount(); column++) {
                startSnapShot(row, column);
            }
        }
    }

    private void startSnapShot(final int row, final int column) {

        MapSnapshotter.Options options = new MapSnapshotter.Options(
                grid.getMeasuredWidth() / grid.getColumnCount(),
                grid.getMeasuredHeight() / grid.getRowCount()
        )
                .withPixelRatio(1)

                .withStyle((column + row) % 2 == 0 ? Style.STREETS : Style.STREETS_DARK)
                .withLocalIdeographFontFamily("sans-serif")
                .withLogo(true);

        if (row % 2 == 0) {
            options.withRegion(new LatLngBounds.Builder()
                    .include(new LatLng(randomInRange(-80, 80), randomInRange(-160, 160)))
                    .include(new LatLng(randomInRange(-80, 80), randomInRange(-160, 160)))
                    .build()
            );
        }

        if (column % 2 == 0) {
            options.withCameraPosition(new CameraPosition.Builder()
                    .target(options.getRegion() != null
                            ? options.getRegion().getCenter()
                            : new LatLng(randomInRange(-80, 80), randomInRange(-160, 160)))
                    .bearing(randomInRange(0, 360))
                    .tilt(randomInRange(0, 60))
                    .zoom(randomInRange(0, 20))
                    .build()
            );
        }

        MapSnapshotter snapshotter = new MapSnapshotter(MapSnapshotActivity.this, options);

        snapshotter.start(snapshot -> {
            ImageView imageView = new ImageView(MapSnapshotActivity.this);
            imageView.setImageBitmap(snapshot.getBitmap());
            grid.addView(
                    imageView,
                    new GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(column))
            );
        });
        snapshotters.add(snapshotter);
    }

    @Override
    public void onPause() {
        super.onPause();

        for (MapSnapshotter snapshotter : snapshotters) {
            snapshotter.cancel();
        }
        snapshotters.clear();
    }

    private static Random random = new Random();

    public static float randomInRange(float min, float max) {
        return (random.nextFloat() * (max - min)) + min;
    }
}
