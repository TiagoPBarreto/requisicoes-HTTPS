package com.curso.requisicoeshttp;

import androidx.annotation.LongDef;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.internal.SafeIterableMap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.curso.requisicoeshttp.api.CEPService;
import com.curso.requisicoeshttp.api.DataService;
import com.curso.requisicoeshttp.model.CEP;
import com.curso.requisicoeshttp.model.Foto;
import com.curso.requisicoeshttp.model.Postagem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private Button botaoRecuperar;
    private TextView textoResultado;
    private Retrofit retrofit;
    private  List<Foto> listaFotos = new ArrayList<>();
    private  List<Postagem> listaPosstagem = new ArrayList<>();
    private DataService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        botaoRecuperar = findViewById(R.id.buttonRecuperar);
        textoResultado = findViewById(R.id.textResultado);
        retrofit = new Retrofit.Builder()
                //.baseUrl("https://viacep.com.br/ws/")
                .baseUrl("https://jsonplaceholder.typicode.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(DataService.class);

        botaoRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //recuperarCEPRetrofit ();
                //recuperarListaRetrofit();

                //salvarPostagem();

                //atualizarPostagem();

                removerPostagem();

                /*
            MyTask task = new MyTask();
            String urlApi = "https://blockchain.info/ticker";
            String cep = "01310100";
            String urlCep = "https://viacep.com.br/ws/"+cep+ "/json/";
            task.execute(urlApi);

                 */

            }
        });

    }
    private void removerPostagem(){
        Call<Void> call = service.removerPostagem(2);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    textoResultado.setText("Status:" + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    private void atualizarPostagem(){
        Postagem postagem = new Postagem("1234", null, "Corpopostagem");
        Call<Postagem> call = service.atualizarPostagemPatch(2,postagem);
        call.enqueue(new Callback<Postagem>() {
            @Override
            public void onResponse(Call<Postagem> call, Response<Postagem> response) {
                if (response.isSuccessful()) {
                    Postagem postagemRespsta =response.body();
                    textoResultado.setText(
                            "Status:" + response.code() +
                                    "id:" + postagemRespsta.getId() +
                                    "useId:" + postagemRespsta.getUserId() +
                                    "titulo: " + postagemRespsta.getTitle() +
                                    "body: " + postagemRespsta.getBody()
                    );
                }
            }

            @Override
            public void onFailure(Call<Postagem> call, Throwable t) {

            }
        });

    }
    private void salvarPostagem(){
        // Configura objeto postagem
        //Postagem postagem = new Postagem("1234", "Titulo postagem!", "Corpopostagem");
        // recupera o serviço e salva postagem

        Call<Postagem> call = service.salvarPostagem("1234", "Titulo postagem!", "Corpopostagem");
        call.enqueue(new Callback<Postagem>() {
            @Override
            public void onResponse(Call<Postagem> call, Response<Postagem> response) {
                if (response.isSuccessful()) {
                    Postagem postagemRespsta =response.body();
                    textoResultado.setText(
                          "Codigo:" + response.code() +
                                  "id:" + postagemRespsta.getId() +
                                  "titulo: " + postagemRespsta.getTitle()
                    );
                }
            }

            @Override
            public void onFailure(Call<Postagem> call, Throwable t) {

            }
        });
    }
    private void recuperarListaRetrofit(){
        DataService service = retrofit.create(DataService.class);
        //Call<List<Foto>> call = service.recuperarFotos();
        Call<List<Postagem>> call = service.recuperarPostagens();
        call.enqueue(new Callback<List<Postagem>>() {
            @Override
            public void onResponse(Call<List<Postagem>> call, Response<List<Postagem>> response) {
                if(response.isSuccessful()){
                    listaPosstagem = response.body();
                    for (int i=0; i<listaPosstagem.size();i++){
                        Postagem postagem = listaPosstagem.get(i);
                        Log.d("resultado", "resultado" + postagem.getId() + "/" + postagem.getTitle());

                    }
                }
            }

            @Override
            public void onFailure(Call<List<Postagem>> call, Throwable t) {

            }
        });
    }

    private void recuperarCEPRetrofit(){
        CEPService cepService = retrofit.create(CEPService.class);
        Call<CEP> call = cepService.recuperarCEP("01310100");
        call.enqueue(new Callback<CEP>() {
            @Override
            public void onResponse(Call<CEP> call, Response<CEP> response) {
                if(response.isSuccessful()){
                    CEP cep = response.body();
                    textoResultado.setText(cep.getLogradouro() + "/" + cep.getBairro() );
                }
            }

            @Override
            public void onFailure(Call<CEP> call, Throwable t) {

            }
        });
    }

    class MyTask extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = strings[0];
            InputStream inputStream = null;
            //inputStreamReader le os dados em Bytes e decodifica para caracteres
            InputStreamReader inputStreamReader=null;
            StringBuffer buffer = null;
            try {
                URL url = new URL(stringUrl);
                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();

                //Recupera em forma de bytes

                inputStream = conexao.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);

                //Objeto utilizado para leitura dos caracteres do InptStreaReader

                BufferedReader reader = new BufferedReader(inputStreamReader);
                buffer = new StringBuffer();
                String linha = "";
                while ((linha = reader.readLine()) !=null){
                    buffer.append(linha);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return buffer.toString();
        }

        @Override
        protected void onPostExecute(String resultado) {
            super.onPostExecute(resultado);
            /*
            String logradouro = null;
            String cep = null;
            String complemento = null;
            String bairro = null;
            String localidade = null;
            String uf = null;

             */
            String objetoValor = null;
            String valorMoeda = null;
            String simbolo = null;

            try {
                /*JSONObject jsonObject = new JSONObject(resultado);
                logradouro = jsonObject.getString("logradouro");
                cep = jsonObject.getString("cep");
                complemento = jsonObject.getString("complemento");
                bairro = jsonObject.getString("bairro");
                localidade = jsonObject.getString("localidade");
                uf = jsonObject.getString("uf");

                 */
                JSONObject jsonObject = new JSONObject(resultado);
                objetoValor = jsonObject.getString("BRL");
                JSONObject jsonObjectReal = new JSONObject(objetoValor);
                valorMoeda = jsonObjectReal.getString("last");
                simbolo = jsonObjectReal.getString("symbol");

            } catch (JSONException e) {
                e.printStackTrace();
            }
           // textoResultado.setText(resultado);
            //textoResultado.setText(logradouro + "/" + cep + "/" + complemento + "/" + bairro + "/" + localidade + "/" + uf );
            textoResultado.setText(simbolo + "" + valorMoeda);

        }
    }
}