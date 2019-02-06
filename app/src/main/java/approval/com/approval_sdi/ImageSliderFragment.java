package approval.com.approval_sdi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import reportku.com.id.R;

/**
 * slider item
 * require: glide, permission internet (if load from internet)
 */
public class ImageSliderFragment extends Fragment {
    private static final String IMAGE_URL = "image_url";

    public static ImageSliderFragment newInstance(String imageUrl) {
        Bundle args = new Bundle();
        args.putString(IMAGE_URL, imageUrl);
        ImageSliderFragment fragment = new ImageSliderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ImageView view = (ImageView) inflater.inflate(R.layout.item_slider, container, false);
        String url = getUrlFromInstance();
        loadImage(view, url);
        return view;
    }

    /**
     * load image use glide
     *
     * @param view view to show image from url
     * @param url  url of image
     */
    private void loadImage(ImageView view, String url) {
        /*Glide.with(this)
                .load(url)
                .thumbnail(0.3f)
                .into(view);*/
        Picasso.with(getActivity()).load(url).into(view);
    }

    /**
     * @return url of image passing from newInstance
     */
    private String getUrlFromInstance() {
        return getArguments().getString(IMAGE_URL);
    }
}