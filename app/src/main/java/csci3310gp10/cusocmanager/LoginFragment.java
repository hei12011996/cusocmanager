package csci3310gp10.cusocmanager;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    View view;
    Button loginButton;
    EditText username;
    EditText password;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        loginButton = (Button) view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = (EditText) view.findViewById(R.id.usernameEditText);
                password = (EditText) view.findViewById(R.id.passwordEditText);
                if (username.getText().toString().equals("jap_admin") &&
                        password.getText().toString().equals("jap_pwd")) {
                    //System.out.println("Login username and password match");
                    Toast.makeText(getContext(), "Login username and password match", Toast.LENGTH_SHORT).show();
                    sharedPref = getContext().getSharedPreferences(
                            getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    editor = sharedPref.edit();
                    editor.putBoolean(getString(R.string.preference_user_has_login), true);
                    editor.apply();
                }
                else {
                    //System.out.println("Invalid login username or password");
                    Toast.makeText(getContext(), "Invalid login username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

}
