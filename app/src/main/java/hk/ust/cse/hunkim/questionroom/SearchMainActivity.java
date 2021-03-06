package hk.ust.cse.hunkim.questionroom;

import android.app.ListActivity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class SearchMainActivity extends ListActivity {

    private String roomBaseUrl;
    private String roomName;
    private Firebase mFirebaseRef;
    private EditText searchText;
    private ImageButton searchButton;
    private HashtagListAdapter mHashtagListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialized once with an Android context.
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_search_main);
        Intent intent = getIntent();

        roomName = intent.getStringExtra("ROOM_NAME");
        roomBaseUrl = intent.getStringExtra("ROOM_BASE_URL");
        mFirebaseRef = new Firebase(roomBaseUrl).child("tags");

    }

    @Override
    public void onStart() {
        super.onStart();

        //GUI design initialization <26/10/2015 by Peter Yeung>
        //This is due to Android default, all buttons are come with capitalized.
        Button quitButton = (Button) findViewById(R.id.close);
        quitButton.setTransformationMethod(null);
        searchText = (EditText) findViewById(R.id.searchInput);
        searchText.setError(null);

        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = getListView();
        // Tell our list adapter that we only want 200 messages at a time
        mHashtagListAdapter = new HashtagListAdapter(
                mFirebaseRef.orderByChild("used").limitToFirst(10),
                this, R.layout.hashtag_search);
        listView.setAdapter(mHashtagListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked, show a toast with the TextView text or do whatever you need.
                String input = ((TextView) view.findViewById(R.id.name)).getText().toString();
                EnterSearchResult(view, input);
                //Log.e("POSITION", "Detect pressed and position is " + String.valueOf(position) + "with id = " + String.valueOf(id));
                //Log.e("POSITION", "Its hashtag is = " + ((TextView) view.findViewById(R.id.name)).getText());
            }
        });

        mHashtagListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                //listView.setSelection(mHashtagListAdapter.getCount() - 1);    NO NEED TO SCROLL DOWN AFTER UPDATING/LOADING LISTVIEW (PETER YEUNG 2015/11/16)
            }
        });

        searchButton = (ImageButton) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String input = searchText.getText().toString();
                        if (!TextUtils.isEmpty(input)) {
                            EnterSearchResult(view, input);
                        } else {
                            searchText.setError(getString(R.string.error_field_required));
                        }//warning to force user input reply
                    }
                }
        );

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String input = searchText.getText().toString();

                    if (TextUtils.isEmpty(input))
                        searchText.setError(getString(R.string.error_field_required));
                    else {
                        EnterSearchResult(view, input);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void EnterSearchResult(View view, String input)
    {
        // Before creating our 'model', we have to replace substring so that prevent code injection
        input = input.replace("<", "&lt;");
        input = input.replace(">", "&gt;");
        Intent intent = new Intent(view.getContext(), SearchResultActivity.class);
        intent.putExtra("ROOM_NAME", roomName);
        intent.putExtra("ROOM_BASE_URL", roomBaseUrl);
        intent.putExtra("SEARCH_INPUT", input);
        view.getContext().startActivity(intent);
        if(view.getContext() instanceof SearchResultActivity)
            finish();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void Close(View view) {
        finish();
    }
}
