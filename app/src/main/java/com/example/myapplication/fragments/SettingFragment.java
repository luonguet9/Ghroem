package com.example.myapplication.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.example.myapplication.R;
import com.example.myapplication.activities.MainActivity;
import com.example.myapplication.activities.SplashActivity;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Spinner spinner;
    RelativeLayout layoutLanguage;
    RelativeLayout layoutTheme;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        layoutLanguage = view.findViewById(R.id.layout_language);
        layoutTheme = view.findViewById(R.id.layout_theme);
        //spinner = view.findViewById(R.id.spinner);

        //showSpinnerSelect();
        layoutLanguage.setOnClickListener(view1 -> {
            showChangeLanguageDialog();
        });

        layoutTheme.setOnClickListener(view1 -> {
            handleChangeTheme();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!SplashActivity.language.equals(Locale.getDefault().getDisplayLanguage())) {
            switch (Locale.getDefault().getDisplayLanguage()) {
                case "English":
                    SplashActivity.langSelect = 0;
                    SplashActivity.language = Locale.getDefault().getDisplayLanguage();
                    break;
                case "Tiếng Việt":
                    SplashActivity.langSelect = 1;
                    SplashActivity.language = Locale.getDefault().getDisplayLanguage();
                    break;
            }
        }
    }


    private void showChangeLanguageDialog() {
        final String[] listLanguage = {"English", "Tiếng Việt"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.choose_language))
                .setSingleChoiceItems(listLanguage, SplashActivity.langSelect, (dialogInterface, i) -> {

                    if (listLanguage[i].equals("English")) {
                        SplashActivity.langSelect = 0;
                        setLocale(requireActivity(), "en");
                        startActivity(requireActivity().getIntent());
                        ((MainActivity) requireActivity()).finish();
                        requireActivity().overridePendingTransition(0, 0);

                    }

                    if (listLanguage[i].equals("Tiếng Việt")) {
                        SplashActivity.langSelect = 1;
                        setLocale(requireActivity(), "vi");
                        startActivity(requireActivity().getIntent());
                        ((MainActivity) requireActivity()).finish();
                        requireActivity().overridePendingTransition(0, 0);

                    }

                })
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .create()
                .show();

    }

//    private void showSpinnerSelect() {
//        final String[] listLanguage = {"Select Language", "English", "Tiếng Việt"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listLanguage);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//        spinner.setSelection(0);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String currentLang = adapterView.getItemAtPosition(i).toString();
//                if (currentLang.equals("English")) {
//                    setLocale(requireActivity(), "en");
//                    ((MainActivity) requireActivity()).finish();
//                    startActivity(requireActivity().getIntent());
//                } else if (currentLang.equals("Tiếng Việt")) {
//                    setLocale(requireActivity(), "vi");
//                    ((MainActivity) requireActivity()).finish();
//                    startActivity(requireActivity().getIntent());
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//    }

    private void setLocale(Activity activity, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

    }

    private void handleChangeTheme() {
        final String[] listTheme = {getString(R.string.light), getString(R.string.dark)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.choose_theme))
                .setSingleChoiceItems(listTheme, SplashActivity.themeSelect, (dialogInterface, i) -> {
                    if (listTheme[i].equals(getString(R.string.light))) {
                        SplashActivity.themeSelect = 0;
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }

                    if (listTheme[i].equals(getString(R.string.dark))) {
                        SplashActivity.themeSelect = 1;
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }
                })
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .create()
                .show();

    }


}