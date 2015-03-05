package finalproject.ece558.edu.pdx.ece.brailleblackjack;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;


/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 */
public class LearnBrailleFragment extends android.support.v4.app.Fragment {
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static LearnBrailleFragment create(int pageNumber) {
        LearnBrailleFragment fragment = new LearnBrailleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public LearnBrailleFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_learn_braille, container, false);

        ImageView  img = (ImageView)rootView.findViewById(R.id.learnBrailImg);

        switch (mPageNumber+1) {
            case 1:

                img.setImageDrawable(getResources().getDrawable(R.drawable.numbers));
                img.setContentDescription(getResources().getString(R.string.description_numbers));
                break;
            case 2:
                img.setImageDrawable(getResources().getDrawable(R.drawable.capital_letters));
                img.setContentDescription(getResources().getString(R.string.description_capitalization));
                break;
            case 3:
                img.setImageDrawable(getResources().getDrawable(R.drawable.letters_atom));
                img.setContentDescription(getResources().getString(R.string.description_letters_atom));
                break;
            case 4:
                img.setImageDrawable(getResources().getDrawable(R.drawable.letters_ntoz));
                img.setContentDescription(getResources().getString(R.string.description_letters_ntoz));
                break;
            default:
                break;
        }

        return rootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}
