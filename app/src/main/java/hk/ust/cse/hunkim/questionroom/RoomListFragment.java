package hk.ust.cse.hunkim.questionroom;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.firebase.client.Firebase;

/**
 * Created by CAI on 17/11/2015.
 */
public class RoomListFragment extends Fragment {
    private String baseUrl;
    private Firebase mFirebaseRef;
    private ListView listView;
    private RoomListAdapter mRoomListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(getActivity());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_room_list, container, false);

        baseUrl = ((JoinActivity)rootView.getContext()).getBaseUrl();

        mFirebaseRef = new Firebase(baseUrl).child("roomList");

        mRoomListAdapter = new RoomListAdapter(
                mFirebaseRef.orderByKey(),
                getActivity(), R.layout.room);

        listView = (ListView)rootView.findViewById(R.id.room_list);
        listView.setAdapter(mRoomListAdapter);
        listView.setFastScrollEnabled(true);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRoomListAdapter.cleanup();
    }
}
