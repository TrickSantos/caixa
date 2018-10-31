package dbv.trick.controledecaixa;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.accountkit.AccountKitLoginResult;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import dbv.trick.controledecaixa.Model.ChecarUsuarioResponse;
import dbv.trick.controledecaixa.Model.Usuario;
import dbv.trick.controledecaixa.Retrofit.ICaixaAPI;
import dbv.trick.controledecaixa.Util.Comum;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1000;
    Button btn_continue;
    ICaixaAPI mService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mService = Comum.getAPI();

        btn_continue = findViewById(R.id.btn_continue);
        btn_continue.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startLogin(LoginType.PHONE);
            }
        });

        if (AccountKit.getCurrentAccessToken() != null){

            final AlertDialog alertDialog = new SpotsDialog(MainActivity.this);
            alertDialog.show();
            alertDialog.setMessage("Aguarde...");

            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(final Account account) {
                    mService.checarUsuarioExists(account.getPhoneNumber().toString())
                            .enqueue(new Callback<ChecarUsuarioResponse>() {
                                @Override
                                public void onResponse(Call<ChecarUsuarioResponse> call, Response<ChecarUsuarioResponse> response) {
                                    ChecarUsuarioResponse checarUsuarioResponse = response.body();

                                    if (checarUsuarioResponse.isExists()){

                                        mService.getUser(account.getPhoneNumber().toString())
                                                .enqueue(new Callback<Usuario>() {
                                                    @Override
                                                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                                                        alertDialog.dismiss();

                                                        Comum.currentUser = response.body();

                                                        startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                                        finish();

                                                    }

                                                    @Override
                                                    public void onFailure(Call<Usuario> call, Throwable t) {
                                                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                                                    }
                                                });



                                    }else{

                                        alertDialog.dismiss();
                                        showRegisterDialog(account.getPhoneNumber().toString());

                                    }
                                }

                                @Override
                                public void onFailure(Call<ChecarUsuarioResponse> call, Throwable t) {

                                }
                            });

                }

                @Override
                public void onError(AccountKitError accountKitError) {
                    Log.d("Erro:", accountKitError.getErrorType().getMessage());

                }
            });

        }

    }

    private void startLogin(LoginType phone) {
        Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder builder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(phone,
                        AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,builder.build());
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE){
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);

            if(result.getError() != null){

                Toast.makeText(this, ""+result.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
            }else if (result.wasCancelled()){
                Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show();
            }else{
                if (result.getAccessToken() != null){
                    final AlertDialog alertDialog = new SpotsDialog(MainActivity.this);
                    alertDialog.show();
                    alertDialog.setMessage("Aguarde...");

                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(final Account account) {
                            mService.checarUsuarioExists(account.getPhoneNumber().toString())
                                    .enqueue(new Callback<ChecarUsuarioResponse>() {
                                        @Override
                                        public void onResponse(Call<ChecarUsuarioResponse> call, Response<ChecarUsuarioResponse> response) {
                                            ChecarUsuarioResponse checarUsuarioResponse = response.body();

                                            if (checarUsuarioResponse.isExists()){

                                                mService.getUser(account.getPhoneNumber().toString())
                                                        .enqueue(new Callback<Usuario>() {
                                                            @Override
                                                            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                                                                alertDialog.dismiss();
                                                                startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                                                finish();

                                                            }

                                                            @Override
                                                            public void onFailure(Call<Usuario> call, Throwable t) {
                                                                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                                                            }
                                                        });



                                            }else{

                                                alertDialog.dismiss();
                                                showRegisterDialog(account.getPhoneNumber().toString());

                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ChecarUsuarioResponse> call, Throwable t) {

                                        }
                                    });

                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {
                            Log.d("Erro:", accountKitError.getErrorType().getMessage());

                        }
                    });

                }
            }

        }
    }

    private void showRegisterDialog(final String telefone) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);


        builder.setTitle("Registrar");
        LayoutInflater inflater = this.getLayoutInflater();
        View cadastro_layout = inflater.inflate(R.layout.cadastro_layout,null);
        final MaterialEditText edt_nome = (MaterialEditText)cadastro_layout.findViewById(R.id.edt_nome);
        final MaterialEditText edt_senha = (MaterialEditText)cadastro_layout.findViewById(R.id.edt_senha);
        final MaterialEditText edt_email = (MaterialEditText)cadastro_layout.findViewById(R.id.edt_email);

        Button btn_cadastrar = (Button)cadastro_layout.findViewById(R.id.btn_cadastrar);

        builder.setView(cadastro_layout);

        final AlertDialog dialog = builder.create();

        btn_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                if (TextUtils.isEmpty(edt_nome.getText().toString())){
                    Toast.makeText(MainActivity.this, "Digite seu nome", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edt_email.getText().toString())){
                    Toast.makeText(MainActivity.this, "Digite seu email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edt_senha.getText().toString())){
                    Toast.makeText(MainActivity.this, "Digite sua senha", Toast.LENGTH_SHORT).show();
                    return;
                }

                final AlertDialog esperaDialog = new SpotsDialog(MainActivity.this);
                esperaDialog.show();
                esperaDialog.setMessage("Aguarde...");

                mService.registrar(edt_nome.getText().toString(),edt_senha.getText().toString(),telefone,edt_email.getText().toString())
                        .enqueue(new Callback<Usuario>() {
                            @Override
                            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                                esperaDialog.dismiss();
                                Usuario usuario = response.body();
                                if (TextUtils.isEmpty(usuario.getMsg_erro())){
                                    Toast.makeText(MainActivity.this, "Usuario Cadastrado", Toast.LENGTH_SHORT).show();
                                    Comum.currentUser = response.body();

                                    startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<Usuario> call, Throwable t) {
                                esperaDialog.dismiss();

                            }
                        });

            }
        });

        dialog.show();
    }

    private void printHeyHash() {
        try{
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);

            for(Signature signature:info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KEYHASH:", Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }

        }catch (PackageManager.NameNotFoundException e){

        } catch (NoSuchAlgorithmException e) {

        }
    }
}
