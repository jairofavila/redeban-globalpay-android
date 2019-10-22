package com.redeban.payment.example.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.redeban.payment.Payment;
import com.redeban.payment.example.R;
import com.redeban.payment.example.utils.Alert;
import com.redeban.payment.example.utils.Constants;
import com.redeban.payment.model.Card;
import com.redeban.payment.rest.TokenCallback;
import com.redeban.payment.rest.model.RedebanError;
import com.redeban.payment.view.CardMultilineWidget;

public class AddCardActivity extends AppCompatActivity {

    Button buttonNext;
    CardMultilineWidget cardWidget;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        mContext = this;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }




        final String uid = Constants.USER_ID;
        final String email = Constants.USER_EMAIL;

        cardWidget = (CardMultilineWidget) findViewById(R.id.card_multiline_widget);
        buttonNext = (Button) findViewById(R.id.buttonAddCard);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonNext.setEnabled(false);

                Card cardToSave = cardWidget.getCard();
                if (cardToSave == null) {
                    buttonNext.setEnabled(true);
                    Alert.show(mContext,
                            "Error",
                            "Invalid Card Data");
                    return;
                }else{
                    final ProgressDialog pd = new ProgressDialog(AddCardActivity.this);
                    pd.setMessage("");
                    pd.show();

                    Payment.addCard(mContext, uid, email, cardToSave, new TokenCallback() {

                        public void onSuccess(Card card) {
                            buttonNext.setEnabled(true);
                            pd.dismiss();
                            if(card != null){
                                if(card.getStatus().equals("valid")){
                                    Alert.show(mContext,
                                            "Card Successfully Added",
                                            "status: " + card.getStatus() + "\n" +
                                                    "Card Token: " + card.getToken() + "\n" +
                                                    "transaction_reference: " + card.getTransactionReference());

                                } else if (card.getStatus().equals("review")) {
                                    Alert.show(mContext,
                                            "Card Under Review",
                                            "status: " + card.getStatus() + "\n" +
                                                    "Card Token: " + card.getToken() + "\n" +
                                                    "transaction_reference: " + card.getTransactionReference());

                                } else {
                                    Alert.show(mContext,
                                            "Error",
                                            "status: " + card.getStatus() + "\n" +
                                                    "message: " + card.getMessage());
                                }


                            }

                            //TODO: Create charge or Save Token to your backend
                        }

                        public void onError(RedebanError error) {
                            buttonNext.setEnabled(true);
                            pd.dismiss();
                            Alert.show(mContext,
                                    "Error",
                                    "Type: " + error.getType() + "\n" +
                                            "Help: " + error.getHelp() + "\n" +
                                            "Description: " + error.getDescription());

                            //TODO: Handle error
                        }

                    });

                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
