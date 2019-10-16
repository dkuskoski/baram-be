package mk.com.barambe.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spannable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mk.com.barambe.ApplicationController;
import mk.com.barambe.R;
import mk.com.barambe.model.Post;
import mk.com.barambe.utils.PicassoImageGetter;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment {


    private static final String TAG = PostFragment.class.getSimpleName();
    private static final String ARG_1 = "arg1";
    private Post post;

    public PostFragment() {
        // Required empty public constructor
    }

    public static PostFragment newInstance(Post post) {

        Bundle args = new Bundle();
        PostFragment fragment = new PostFragment();
        args.putString(ARG_1, ApplicationController.getGson().toJson(post));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        post = ApplicationController.getGson()
                .fromJson(getArguments().getString(ARG_1), Post.class);
        return init(inflater.inflate(R.layout.fragment_post, container, false));
    }

    @SuppressLint("SetJavaScriptEnabled")
    private View init(View v) {
        ImageView imageView = v.findViewById(R.id.post_image);
        LinearLayout wrapper = v.findViewById(R.id.post_wrapper);
        TextView title = v.findViewById(R.id.post_title);
        TextView contentHolder = v.findViewById(R.id.post_content);
        TextView date = v.findViewById(R.id.post_date);
        TextView views = v.findViewById(R.id.post_views);

        Picasso.get().load(post.getImage()).into(imageView);
        title.setText(post.getTitle());
        date.setText(getString(R.string.from,
                post.getCreated_at().substring(0, 11)));
        views.setText(getString(R.string.viewed, String.valueOf(post.getViews())));
        setVideos(wrapper);
        setContent(contentHolder);
        return v;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setVideos(LinearLayout wrapper) {
        Matcher matcher = Pattern.compile("(?i)<iframe[^>]+?src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>")
                .matcher(post.getContent());
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        while (matcher.find()) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    width, width / 18 * 9);

            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            Log.d(TAG, "setVideos: " + matcher.group(1));
            WebView webView = new WebView(getActivity());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setLayoutParams(layoutParams);
            webView.loadUrl(matcher.group(1));
            wrapper.addView(webView);
        }
    }

    private void setContent(TextView contentHolder) {
        PicassoImageGetter imageGetter = new PicassoImageGetter(contentHolder);
        String content = post.getContent();

        content = content.replaceAll("<iframe\\s+.*?\\s+src=(\".*?\").*?<\\/iframe>", "");
        Spannable html;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            html = (Spannable) Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY, imageGetter, null);
        } else {
            html = (Spannable) Html.fromHtml(content, imageGetter, null);
        }
        contentHolder.setText(html);
    }

}
