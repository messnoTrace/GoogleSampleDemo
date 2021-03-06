package com.notrace.activitytransition;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.widget.ImageView;
import android.widget.TextView;

import com.notrace.R;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    // Extra name for the ID parameter
    public static final String EXTRA_PARAM_ID = "detail:_id";

    // View name of the header image. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_IMAGE = "detail:header:image";

    // View name of the header title. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_TITLE = "detail:header:title";

    private ImageView mHeaderImageView;
    private TextView mHeaderTitle;

    private Item mItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
                // Retrieve the correct Item instance, using the ID provided in the Intent
                mItem = Item.getItem(getIntent().getIntExtra(EXTRA_PARAM_ID, 0));

                mHeaderImageView = (ImageView) findViewById(R.id.imageview_header);
                mHeaderTitle = (TextView) findViewById(R.id.textview_title);

                /**
                 * Set the name of the view's which will be transition to, using the static values above.
                 * This could be done in the layout XML, but exposing it via static variables allows easy
                 * querying from other Activities
                 */
                ViewCompat.setTransitionName(mHeaderImageView, VIEW_NAME_HEADER_IMAGE);
                ViewCompat.setTransitionName(mHeaderTitle, VIEW_NAME_HEADER_TITLE);

                loadItem();
            }

            private void loadItem() {
                // Set the title TextView to the item's name and author
                mHeaderTitle.setText(getString(R.string.image_header, mItem.getName(), mItem.getAuthor()));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && addTransitionListener()) {
                    // If we're running on Lollipop and we have added a listener to the shared element
                    // transition, load the thumbnail. The listener will load the full-size image when
                    // the transition is complete.
                    loadThumbnail();
                } else {
                    // If all other cases we should just load the full-size image now
                    loadFullSizeImage();
                }
            }

            /**
             * Load the item's thumbnail image into our {@link ImageView}.
             */
            private void loadThumbnail() {
                Picasso.with(mHeaderImageView.getContext())
                        .load(mItem.getThumbnailUrl())
                        .noFade()
                        .into(mHeaderImageView);
            }

            /**
             * Load the item's full-size image into our {@link ImageView}.
             */
            private void loadFullSizeImage() {
                Picasso.with(mHeaderImageView.getContext())
                        .load(mItem.getPhotoUrl())
                        .noFade()
                        .noPlaceholder()
                        .into(mHeaderImageView);
            }

            /**
             * Try and add a {@link Transition.TransitionListener} to the entering shared element
             * {@link Transition}. We do this so that we can load the full-size image after the transition
             * has completed.
             *
             * @return true if we were successful in adding a listener to the enter transition
             */
            private boolean addTransitionListener() {
                final Transition transition = getWindow().getSharedElementEnterTransition();

                if (transition != null) {
                    // There is an entering shared element transition so add a listener to it
                    transition.addListener(new Transition.TransitionListener() {
                        @Override
                        public void onTransitionEnd(Transition transition) {
                            // As the transition has ended, we can now load the full-size image
                            loadFullSizeImage();

                            // Make sure we remove ourselves as a listener
                            transition.removeListener(this);
                        }

                        @Override
                        public void onTransitionStart(Transition transition) {
                            // No-op
                        }

                        @Override
                        public void onTransitionCancel(Transition transition) {
                            // Make sure we remove ourselves as a listener
                            transition.removeListener(this);
                        }

                        @Override
                        public void onTransitionPause(Transition transition) {
                            // No-op
                        }

                        @Override
                        public void onTransitionResume(Transition transition) {
                            // No-op
                        }
                    });
                    return true;
                }

                // If we reach here then we have not added a listener
                return false;
            }
}
