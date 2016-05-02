package edu.uw.tcss450.team1.cosmic_kids_game.main;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import edu.uw.tcss450.team1.cosmic_kids_game.R;

public class MainActivity extends FragmentActivity implements
        MainFragment.OnFragmentInteractionListener,
        OptionsFragment.OnFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener {

    public enum Fragments {
        Main, Options, Login, Register
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            MainFragment firstFragment = new MainFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
    }

    @Override
    public void onFragmentInteraction(Fragments fragment) {
        switch (fragment) {
            case Main:
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, (new MainFragment())).commit();
                break;
            case Options:
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, (new OptionsFragment())).commit();
                break;
            case Login:
                break;
            case Register:
                break;
            default:
                break;
        }
    }

    public void launchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
    }

    public void onOptionsButtonPressed(View view) {
        OptionsFragment fragment = new OptionsFragment();
        launchFragment(fragment);
    }

    public void onRegisterButtonPressed(View view) {
        RegisterFragment fragment = new RegisterFragment();
        launchFragment(fragment);
    }

    public void onMultiplayerButtonPressed(View view) {
        Boolean isRegistered = false;
        // TODO Change above to a check on login/registered
        if (!isRegistered) {
            Toast.makeText(MainActivity.this, "You must register first!", Toast.LENGTH_SHORT).show();
            RegisterFragment fragment = new RegisterFragment();
            launchFragment(fragment);
        }
    }

    public void onLoginButtonPressed(View view) {
        LoginFragment fragment = new LoginFragment();
        launchFragment(fragment);
    }

    public void registerUser(View view) {
        String email = ((EditText)findViewById(R.id.email_input)).getText().toString();
        String user = ((EditText)findViewById(R.id.user_input)).getText().toString();
        String pass = ((EditText)findViewById(R.id.pass_input)).getText().toString();
        String pass2 = ((EditText)findViewById(R.id.pass_verify_input)).getText().toString();
        if (!pass.equals(pass2)) {
            Toast.makeText(MainActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            ((EditText)findViewById(R.id.pass_input)).setText("");
            ((EditText)findViewById(R.id.pass_verify_input)).setText("");
        } else if (pass.length() < 6) {
            Toast.makeText(MainActivity.this, "Password must be at least 6 characters long",
                    Toast.LENGTH_SHORT).show();
        } else {
            try {
                int n = Integer.parseInt(pass);
            } catch (Exception e) {
            }
            boolean hasUpper = false;
            boolean hasDigit = false;
            for (char c : pass.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    hasUpper = true;
                }
                if (Character.isDigit(c)) {
                    hasDigit = true;
                }
            }
            if (!hasUpper) {
                Toast.makeText(MainActivity.this, "Password must contain at least one uppercase",
                        Toast.LENGTH_SHORT).show();
            } else if (!hasDigit) {
                Toast.makeText(MainActivity.this, "Password must contain at least one number",
                        Toast.LENGTH_SHORT).show();
            } else{
                // TODO send registration
                Toast.makeText(MainActivity.this, "Registration successful!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}