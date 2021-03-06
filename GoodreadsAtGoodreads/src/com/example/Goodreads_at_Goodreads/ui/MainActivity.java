package com.example.Goodreads_at_Goodreads.ui;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import com.example.Goodreads_at_Goodreads.R;
import com.example.Goodreads_at_Goodreads.models.Book;
import com.example.Goodreads_at_Goodreads.requests.GetBookMetadata;
import com.example.Goodreads_at_Goodreads.ui.fragments.BookListFragment;
import com.example.Goodreads_at_Goodreads.ui.fragments.SingleBookFragment;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.List;

import static com.example.Goodreads_at_Goodreads.utils.Constants.BASE_URL;

public class MainActivity extends Activity {

    private static final String URL = "http://glasswaves.co";

    // TODO-XX: When searching is up and running
//    private EditText mBookTitle;
    private Switch mBatchSwitch;

    private List<String> mScannedISBNs;

    private boolean mWantsToBatchAdd = false;

    private View.OnClickListener batchSwitchListener = new
            View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    mWantsToBatchAdd = mBatchSwitch.isChecked();
                }
            };
    private View.OnClickListener searchButtonListener = new
            View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    // TODO-XX: When searching is up and running
//                    String bookTitle = mBookTitle.getText().toString();
                    // Fire off Async Task here to search!
                }
            };
    private View.OnClickListener scanOnClick =
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(
                            "com.google.zxing.client.android.SCAN");
                    startActivityForResult(intent, 0);
                }
            };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button mScanButton = (Button) findViewById(R.id.startCapture);
        Button mSearchButton = (Button) findViewById(R.id.search);
        // TODO-XX: When searching is up and running
//        mBookTitle = (EditText) findViewById(R.id.bookTitle);
        mBatchSwitch = (Switch) findViewById(R.id.batchAdder);

        mScanButton.setOnClickListener(scanOnClick);
        mBatchSwitch.setOnClickListener(batchSwitchListener);
        mSearchButton.setOnClickListener(searchButtonListener);

        mScannedISBNs = new ArrayList<String>();
//        displaySingleBook("9780439554930");
        displayBookList(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                // Handle successful scan
                Log.d("check", contents);
                Log.d("check", format);
                int numbers = contents.length();
                if ((numbers != 10 && numbers != 13)
                        || !"EAN_13".equals(format)) {
                    Toast t = Toast.makeText(this, "Bad Barcode!",
                            Toast.LENGTH_SHORT);
                    t.show();
                } else {
                    mScannedISBNs.add(contents);
                    if (mScannedISBNs.size() > 9) {
                        addAllToCatalog();
                    }
                    Toast t = Toast.makeText(this, "Collected!",
                            Toast.LENGTH_SHORT);
                    t.show();

                    if (mWantsToBatchAdd) {
                        startActivityForResult(intent, 0);
                    } else {
//                        requestSingleMetaData(contents);
                        displaySingleBook(contents);
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("check", "CANCELLED");
                Toast t;
                if (!mScannedISBNs.isEmpty()) {
                    addAllToCatalog();
                    t = Toast.makeText(this, "Batch Sent!",
                            Toast.LENGTH_SHORT);

                } else {
                    t = Toast.makeText(this, "No barcodes scanned",
                            Toast.LENGTH_SHORT);
                }
                t.show();
            }

        }
    }

    /**
     * Upload all the books currently in {@link #mScannedISBNs}
     */
    private void addAllToCatalog() {
        // View state add spinner
        mScannedISBNs.clear();
    }

    private void displaySingleBook(String isbn) {
        SingleBookFragment singleBookFragment =
                SingleBookFragment.newInstance(isbn);
        FragmentTransaction transaction =
                getFragmentManager().beginTransaction();
        transaction.replace(R.id.book_display_container,
                singleBookFragment).commit();
    }

    private void displayBookList(boolean upload) {
        BookListFragment singleBookFragment =
                BookListFragment.newInstance(upload);
        FragmentTransaction transaction =
                getFragmentManager().beginTransaction();
        transaction.replace(R.id.book_display_container,
                singleBookFragment).commit();
    }
}
