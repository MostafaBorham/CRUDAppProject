package com.borham.simplecrud;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView = null;
    LayoutInflater inflater = null;
    View dialogView = null;
    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    EditText idInput, nameInput, ageInput, cityInput = null;
    MySqlliteDatabase database = null;
    LinearLayout layout = null;
    ArrayList<DatabaseFormat> data = null;
    CustomAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = new MySqlliteDatabase(this);
        recyclerView = findViewById(R.id.crudRV);
        builder = new AlertDialog.Builder(this);
        inflater = this.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.input_data_dialog, null);
        idInput = dialogView.findViewById(R.id.idInput);
        nameInput = dialogView.findViewById(R.id.nameInput);
        ageInput = dialogView.findViewById(R.id.ageInput);
        cityInput = dialogView.findViewById(R.id.cityInput);
        layout = dialogView.findViewById(R.id.rootTxts);
    }

    @Override
    protected void onStart() {
        super.onStart();
        createDialog();
        data = database.getAllEntry();
        adapter = new CustomAdapter(data);
    }

    public void createInList(View view) {
        controlInputsVisibility(View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE, 3f);
        showDialog("create");

    }

    public void updateToList(View view) {
        if (CustomAdapter.row_index >= data.size()) CustomAdapter.row_index = -1;
        if (CustomAdapter.row_index < 0) {
            controlInputsVisibility(View.VISIBLE, View.GONE, View.GONE, View.GONE, 1f);
            showDialog("fetch data");
        } else {
            setTextToInputs(data.get(CustomAdapter.row_index).getId() + "", data.get(CustomAdapter.row_index).getName(), data.get(CustomAdapter.row_index).getAge(), data.get(CustomAdapter.row_index).getCity());
            controlInputsVisibility(View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE, 3f);
            showDialog("update");

        }
    }

    public void readFromList(View view) {
        CustomAdapter.row_index = -1;
        if (data.isEmpty()) {
            Toast.makeText(this, getString(R.string.nodata_error), Toast.LENGTH_SHORT).show();
            recyclerView.setAdapter(null);
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }
    }

    public void deleteFromList(View view) {
        if (CustomAdapter.row_index >= data.size()) CustomAdapter.row_index = -1;
        if (CustomAdapter.row_index < 0) {
            controlInputsVisibility(View.VISIBLE, View.GONE, View.GONE, View.GONE, 1f);
            showDialog("delete");
        } else {
            database.deleteEntry(data.get(CustomAdapter.row_index).getId() + "");
            Toast.makeText(MainActivity.this, getString(R.string.delete_notify), Toast.LENGTH_SHORT).show();
            data.remove(CustomAdapter.row_index);
            adapter.notifyDataSetChanged();
        }
    }

    private void createDialog() {
        alertDialog = builder
                .setView(dialogView)
                .setPositiveButton(" ", null) //Set to null. We override the onclick
                .setNegativeButton(R.string.neg_btn, (dialog, which) -> {
                    idInput.setError(null);
                    nameInput.setError(null);
                    ageInput.setError(null);
                    cityInput.setError(null);
                    setTextToInputs("", "", "", "");
                })
                .setCancelable(false)
                .create();
    }

    private void showDialog(String positiveBtnName) {
        alertDialog.setOnShowListener(dialog -> {
            Button posBtn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            posBtn.setText(positiveBtnName);
            posBtn.setOnClickListener(view -> {
                switch (positiveBtnName) {
                    case "create":
                        if (checkValidation(new EditText[]{nameInput, ageInput, cityInput})) {
                            long l = database.addEntry(new DatabaseFormat(nameInput.getText().toString().trim(), cityInput.getText().toString().trim(), ageInput.getText().toString().trim()));
                            Toast.makeText(MainActivity.this, getString(R.string.create_notify) + l, Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                            data.add(new DatabaseFormat((int) l, nameInput.getText().toString(), cityInput.getText().toString(), ageInput.getText().toString()));
                            adapter.notifyDataSetChanged();
                            setTextToInputs("", "", "", "");
                        }
                        break;
                    case "fetch data":
                        if (checkValidation(new EditText[]{idInput})) {
                            DatabaseFormat format = database.getEntry(idInput.getText().toString());
                            setTextToInputs(idInput.getText().toString(), format.getName(), format.getAge(), format.getCity());
                            alertDialog.dismiss();
                            controlInputsVisibility(View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE, 3f);
                            showDialog("update");
                        }
                        break;
                    case "update":
                        if (checkValidation(new EditText[]{nameInput, ageInput, cityInput})) {
                            database.updateEntry(idInput.getText().toString(), new DatabaseFormat(nameInput.getText().toString().trim(), cityInput.getText().toString().trim(), ageInput.getText().toString().trim()));
                            Toast.makeText(MainActivity.this, getString(R.string.update_notify), Toast.LENGTH_LONG).show();
                            alertDialog.dismiss();
                            operateOnRecycleView("update");
                            adapter.notifyDataSetChanged();
                            setTextToInputs("", "", "", "");
                        }
                        break;
                    case "delete":
                        if (checkValidation(new EditText[]{idInput})) {
                            database.deleteEntry(idInput.getText().toString());
                            Toast.makeText(MainActivity.this, getString(R.string.delete_notify), Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                            operateOnRecycleView("delete");
                            adapter.notifyDataSetChanged();
                            idInput.setText("");
                        }
                        break;
                }
            });
        });
        alertDialog.show();
    }

    private boolean checkValidation(EditText[] inputs) {
        boolean result = true;
        String txtTmp;
        for (EditText text : inputs) {
            txtTmp = text.getText().toString().trim();
            if (txtTmp.isEmpty()) {
                text.setError(getString(R.string.nullity_error));
                result = false;
            } else {
                if (text.getId() == R.id.idInput) {
                    if (!database.searchEntry(txtTmp)) {
                        text.setError(getString(R.string.id_incorrect_error));
                        result = false;
                    }
                } else if (txtTmp.length() < 3) {
                    if (text.getId() == R.id.nameInput) {
                        text.setError(getString(R.string.nameTxt_error));
                        result = false;
                    } else if (text.getId() == R.id.cityInput) {
                        text.setError(getString(R.string.cityTxt_error));
                        result = false;
                    }
                }
            }
        }
        return result;
    }

    private void operateOnRecycleView(String operation) {
        if (operation.equals("update")) {
            for (DatabaseFormat row : data) {
                if (row.getId() == Integer.parseInt(idInput.getText().toString())) {
                    row.setName(nameInput.getText().toString());
                    row.setCity(cityInput.getText().toString());
                    row.setAge(ageInput.getText().toString());
                    break;
                }
            }
        } else {
            for (DatabaseFormat row : data) {
                if (row.getId() == Integer.parseInt(idInput.getText().toString())) {
                    data.remove(row);
                    break;
                }
            }
        }
    }

    private void controlInputsVisibility(int id_state, int name_state, int age_state, int city_state, float layout_weightSum) {
        idInput.setVisibility(id_state);
        nameInput.setVisibility(name_state);
        ageInput.setVisibility(age_state);
        cityInput.setVisibility(city_state);
        layout.setWeightSum(layout_weightSum);
    }

    private void setTextToInputs(String id, String name, String age, String city) {
        idInput.setText(id);
        nameInput.setText(name);
        ageInput.setText(age);
        cityInput.setText(city);
    }
}