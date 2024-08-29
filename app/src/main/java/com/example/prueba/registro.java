package com.example.prueba;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.regex.Pattern;

public class registro extends AppCompatActivity {
    EditText nombreEditText;
    EditText apellidoEditText;
    EditText fechaEditText;
    Button siguiente;
    Button cancelar;
    RadioButton hombre;
    RadioButton mujer;
    RadioButton otro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);

        nombreEditText = findViewById(R.id.editTextText3);
        apellidoEditText = findViewById(R.id.editTextText4);
        fechaEditText = findViewById(R.id.text_calendario);

        siguiente = findViewById(R.id.button_sigue_registro);
        cancelar = findViewById(R.id.button_cancelar_registro);
        hombre = findViewById(R.id.hombre);
        mujer = findViewById(R.id.mujer);
        otro = findViewById(R.id.otro);

        InputFilter soloLetras = new InputFilter() {
            Pattern pattern = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ]*$");

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (!pattern.matcher(source).matches()) {
                    return "";
                }
                return null;
            }
        };

        nombreEditText.setFilters(new InputFilter[]{soloLetras});
        apellidoEditText.setFilters(new InputFilter[]{soloLetras});

        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarFormulario()) {
                    String nombre = nombreEditText.getText().toString().trim();
                    String apellido = apellidoEditText.getText().toString().trim();
                    String fecha = fechaEditText.getText().toString().trim();
                    String genero = hombre.isChecked() ? "Hombre" : mujer.isChecked() ? "Mujer" : "Otro";

                    SharedPreferences sharedPreferences = getSharedPreferences("UserRegistration", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("nombre_registro", nombre);
                    editor.putString("apellido_registro", apellido);
                    editor.putString("fecha_registro", fecha);
                    editor.putString("genero_registro", genero);
                    editor.apply();

                    Intent intent = new Intent(registro.this, registro_2.class);
                    startActivity(intent);
                }
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(registro.this, MainActivity.class);
                startActivity(intent);
            }
        });

        fechaEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrircalendario(view);
            }
        });
    }

    private boolean validarFormulario() {
        String nombre = nombreEditText.getText().toString().trim();
        String apellido = apellidoEditText.getText().toString().trim();
        String fecha = fechaEditText.getText().toString().trim();
        boolean generoSeleccionado = hombre.isChecked() || mujer.isChecked() || otro.isChecked();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese su nombre.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (apellido.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese su apellido.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (fecha.isEmpty()) {
            Toast.makeText(this, "Por favor, seleccione su fecha de nacimiento.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!generoSeleccionado) {
            Toast.makeText(this, "Por favor, seleccione su género.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void abrircalendario(View view) {
        Calendar cal = Calendar.getInstance();
        int ano = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH);
        int dia = cal.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(registro.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String fecha = dayOfMonth + "/" + (month + 1) + "/" + year;
                fechaEditText.setText(fecha);
            }
        }, ano, mes, dia);
        dpd.show();
    }
}