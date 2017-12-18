package csci3310gp10.cusocmanager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class MemberDetailFragment extends Fragment {
    private Member member = null;
    EditText chinese_name_text = null;
    EditText english_name_text = null;
    EditText sid_text = null;
    EditText college_text = null;
    EditText major_year_text = null;
    EditText phone_text = null;
    EditText email_text = null;


    public MemberDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_detail, container, false);
        Bundle args = getArguments();
        member = args.getParcelable("member");
        chinese_name_text = (EditText) view.findViewById(R.id.chinese_name_text);
        english_name_text = (EditText) view.findViewById(R.id.english_name_text);
        sid_text = (EditText) view.findViewById(R.id.sid_text);
        college_text = (EditText) view.findViewById(R.id.college_text);
        major_year_text = (EditText) view.findViewById(R.id.major_year_text);
        phone_text = (EditText) view.findViewById(R.id.phone_text);
        email_text = (EditText) view.findViewById(R.id.email_text);
        closeAllTextEdit();
        insertMemberInfo();

        // Inflate the layout for this fragment
        return view;
    }

    private void insertMemberInfo(){
        chinese_name_text.setText(member.getChineseName());
        english_name_text.setText(member.getEnglishName());
        sid_text.setText(member.getSID());
        college_text.setText(member.getCollege());
        major_year_text.setText(member.getMajorYear());
        phone_text.setText(member.getPhone());
        email_text.setText(member.getEmail());
    }

    private void closeAllTextEdit(){
        chinese_name_text.setEnabled(false);
        english_name_text.setEnabled(false);
        sid_text.setEnabled(false);
        college_text.setEnabled(false);
        major_year_text.setEnabled(false);
        phone_text.setEnabled(false);
        email_text.setEnabled(false);
    }

}
