package com.akashorderandpickup.akashadminonp.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.akashorderandpickup.akashadminonp.R;
import com.akashorderandpickup.akashadminonp.model.AppConstants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AppUtils {
    public static final String TAG = "AppUtils";

    public static final String USER = "users";
    public static final String SHOP = "shops";
    public static final String ORDER = "orders";
    public static final String FEEDBACK = "feedback";
    public static final String PAYMENT = "payments";
    public static final String PAYMENT_REQUEST = "payments_requests";
    public static final String NUMBER_95 = "+919521751724";
    public static final String NUMBER_96 = "+919660767800";

    public static final String PAYMENT_METHOD = "payment_method";

    public static final int EMAIL = 0;
    public static final int WHATSAPP_US = 1;
    public static final int WHATSAPP_95 = 2;
    public static final int WHATSAPP_96 = 3;
    public static final int CALL_95 = 4;
    public static final int CALL_96 = 5;
    public static String phoneNumberString = "+919521751724";

    /**
     *
     * @param userORshops to identify the first child
     */
    public static void sendToFirebaseDatabase(String userORshops, String orderORpayments, String uid, Map<String, Object> values){
        Calendar calendar = Calendar.getInstance();
        Integer y =  calendar.get(Calendar.YEAR);
        Integer d = calendar.get(Calendar.DATE);
        String monthName = new SimpleDateFormat("MMM").format(calendar.getTime());
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(y.toString()).child(monthName).child(d.toString())
                .child(userORshops)//.child("shops")
                .child(orderORpayments)//.child("feedback")
                .child(uid)
                .child(String.valueOf(System.currentTimeMillis()))
                .setValue(values).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //Success
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed
                Log.w(TAG, "Failed sending to realtime db: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    public static void rateOnPlaystore(Context ctx) {
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse(AppConstants.ONP_APP_LINK));
        try {
            ctx.startActivity(new Intent(intent)
                    .setPackage("com.android.vending"));
        } catch (android.content.ActivityNotFoundException exception) {
            ctx.startActivity(intent);
        }
    }
    public static void openPlaystore(Context ctx, String APP_PACKAGE_NAME) {
        String AppLink = "https://play.google.com/store/apps/details?id=" + APP_PACKAGE_NAME;
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse(AppLink));
        try {
            ctx.startActivity(new Intent(intent)
                    .setPackage("com.android.vending"));
        } catch (android.content.ActivityNotFoundException exception) {
            ctx.startActivity(intent);
        }
    }

    public static void sendEmail(Context ctx, String subject, String body) {
//                .withUsername("leegumindia@gmail.com")
//                .withPassword("leegumindia@2021")
//                .withUsername("orderonlineandpickup@gmail.com")
//                .withPassword("Akash&team_ONP@2021")
        Log.w("TAG: AppUtils", "request to send email");
//        BackgroundMail.newBuilder(ctx)
//                .withUsername("orderonlineandpickup@gmail.com")
//                .withPassword("Akash&team_ONP@2021")
//                .withMailto("orders@leegum.in")
//                .withType(BackgroundMail.TYPE_PLAIN)
//                .withUsername("LEEGUM INDIA")
//                .withSubject(subject)
//                .withBody(body)
//                .withProcessVisibility(true)//Enable it to see progressBar
//                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
//                    @Override
//                    public void onSuccess() {
//                        //email sent successfully
//                        //Toast.makeText(ctx, "email sent successfully", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
//                    @Override
//                    public void onFail() {
//                        //email sending failed
//                        //Toast.makeText(ctx, "email failed", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .send();
    }

    public static void showAlert(Context ctx, String title, String msg, String posBtnMsg, Boolean IsCancelable, Boolean IsNegativeBtn, String negBtnMsg){

        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx, R.style.AlertDialogTheme);
        dialog.setTitle(title);
        //dialog.setIcon(R.drawable.onp_new_logo_with_ring);
        dialog.setCancelable(IsCancelable);
        dialog.setMessage(msg);

        if(posBtnMsg.isEmpty())
            posBtnMsg = ctx.getResources().getString(R.string.ok_btn);

        String finalPosBtnMsg = posBtnMsg;
        dialog.setPositiveButton(posBtnMsg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(finalPosBtnMsg.equalsIgnoreCase("Copy Email")){
                    copyToClipboard(ctx, "Leegum Email", "leegumindia@gmail.com", true, "", Toast.LENGTH_SHORT);
                }else if(finalPosBtnMsg.equalsIgnoreCase("Copy Number")){
                    copyToClipboard(ctx, "Leegum Contact Number", phoneNumberString, true, "", Toast.LENGTH_SHORT);
                }
            }
        });

        if(IsNegativeBtn) {
            dialog.setNegativeButton(negBtnMsg, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(negBtnMsg == ctx.getString(R.string.check_app_update))
                        rateOnPlaystore(ctx);

                    dialog.dismiss();
                    dialog.cancel();
                }
            });
        }

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();

    }

    /**
     *  Paste below function in the activity where you want use to rate app in the playstore
     */
//    void askRatings() {
//        ReviewManager manager = ReviewManagerFactory.create(this);
//        Task<ReviewInfo> request = manager.requestReviewFlow();
//        request.addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                // We can get the ReviewInfo object
//                ReviewInfo reviewInfo = task.getResult();
//                Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
//                flow.addOnCompleteListener(task2 -> {
//                    // The flow has finished. The API does not indicate whether the user
//                    // reviewed or not, or even whether the review dialog was shown. Thus, no
//                    // matter the result, we continue our app flow.
//                });
//            } else {
//                // There was some problem, continue regardless of the result.
//            }
//        });
//    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    public static void customerSupport(Context ctx, String phoneNumber, int MethodIdentifier, String msg, String subject){
        switch (MethodIdentifier){
            case WHATSAPP_US://US(+1) PhoneNumber Passed as 1st parameter fetched from Database
                if(phoneNumber==null || phoneNumber.isEmpty()){
                    initiateChatOnWhatsapp(ctx, NUMBER_95, msg);
                }else{
                    initiateChatOnWhatsapp(ctx, phoneNumber, msg);
                }
                break;
            case WHATSAPP_95:
                initiateChatOnWhatsapp(ctx, NUMBER_95, msg);
                break;
            case WHATSAPP_96:
                initiateChatOnWhatsapp(ctx, NUMBER_96, msg);
                break;
            case CALL_95:
                initiateCall(ctx, NUMBER_95);
                break;
            case CALL_96:
                initiateCall(ctx, NUMBER_96);
                break;
            case EMAIL:
            default:
                initiateEmailIntent(ctx, subject, msg);
        }

    }

    public static void initiateCall(Context ctx, String phoneNum){
        Intent callIntent = new Intent(Intent.ACTION_VIEW);
        try{
            callIntent.setData(Uri.parse("tel:" + phoneNum));
            ctx.startActivity(callIntent);
        }catch(Exception exception){
            exception.printStackTrace();
            Log.w(TAG, exception.getMessage());
            callIntent.setData(Uri.parse("tel:" + "+919521751724"));
            ctx.startActivity(callIntent);
        }
    }
    public static void initiateChatOnWhatsapp(Context ctx, String phoneNum, String msg){
        Intent chatIntent = new Intent(Intent.ACTION_VIEW);
        String trimphoneNum = phoneNum.substring(1);
        chatIntent.setPackage("com.whatsapp");
        chatIntent.setData(Uri.parse("https://wa.me/"+ trimphoneNum +"?text="+ msg));
        try{
        if(chatIntent.resolveActivity(ctx.getPackageManager()) != null){
            ctx.startActivity(chatIntent);
        }else{
            chatIntent.setPackage("com.whatsapp.w4b");
            if(chatIntent.resolveActivity(ctx.getPackageManager()) != null){
                ctx.startActivity(chatIntent);
            }else{
                chatIntent.setPackage(null);
                ctx.startActivity(chatIntent);
            }
        }
        }catch (Exception e){
            phoneNumberString = phoneNum;
            showAlert(ctx, "Some error occured", "Send a whatsapp message to us at " + phoneNum,"Copy Number", true, false, "");
        }
    }
    public static void initiateEmailIntent(Context ctx, String subject, String msg){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"leegumindia@gmail.com","orders@leegum.in"});
        String finalmsg = "\n\n\n\n\n\n\n" + msg; //7 lines of gap for user to type msg
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, finalmsg);
        emailIntent.putExtra(Intent.ACTION_SENDTO, "leegumindia@gmail.com");
        //emailIntent.putExtra(Intent.EXTRA_CC, "orders@leegum.in");
        try {
            ctx.startActivity(emailIntent);
        }catch (Exception e){
            showAlert(ctx, "Some error occured", "Email us to leegumindia@gmail.com","Copy Email", true, false, "");
        }
    }

    public static void copyToClipboard(Context ctx, String label, String msg, Boolean showCopiedToast, String customCopiedToastMessage, int toastLenght){
        ClipboardManager clipboard = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, msg);
        clipboard.setPrimaryClip(clip);

        if(showCopiedToast) {
            if(customCopiedToastMessage==null || customCopiedToastMessage.isEmpty())
                customCopiedToastMessage = "Copied!";
            Toast.makeText(ctx, customCopiedToastMessage, toastLenght).show();
        }
    }

    public static void openCustomerSupport(Context ctx, String TAG, String msg){
        Intent callIntent = new Intent(Intent.ACTION_VIEW);
        callIntent.setPackage("com.whatsapp");
        if(msg ==null || msg.isEmpty()){
            callIntent.setData(Uri.parse("https://wa.me/message/WF43HOX2MVAOB1"));
        }else {
            callIntent.setData(Uri.parse("https://wa.me/919521751724?text="+ msg));
        }
        try{
            ctx.startActivity(callIntent);
        }catch (Exception e){
            callIntent.setPackage(null);
            callIntent.setPackage("com.whatsapp.w4b");
            try{
                ctx.startActivity(callIntent);
            }catch (Exception e1){
                Log.w(TAG, e1.getMessage());
                Intent phoneCallIntent = new Intent(Intent.ACTION_VIEW);
                try{
                    callIntent.setData(Uri.parse("tel:" + "+919521751724"));
                    ctx.startActivity(phoneCallIntent);
                }catch(Exception exception){
                    Log.w(TAG, exception.getMessage());
                    //showAlert(ctx, "Some error occured", "Email us to leegumindia@gmail.com","Email", true, true, "Cancel");
                    //callIntent.setData(Uri.parse("tel:" + "+919660767800"));
                    //ctx.startActivity(phoneCallIntent);
                }
            }
        }
    }

    public static void showTodoToast(Context ctx) {
        Toast.makeText(ctx, "TODO: Implement", Toast.LENGTH_SHORT).show();
    }

    public static void showTodoToast(Context ctx, String str) {
        Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show();
    }

    public static ArrayList<String> createIndex(String text, Boolean creatBurstSplits) {
        ArrayList<String> index = new ArrayList<>();

        text = text.trim().toLowerCase(Locale.ROOT);
        while (text.contains("  "))
            text = text.trim().replace("  ", " ");

        index.add(text);

        String[] wordsToRemove = {"a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", "is", "it", "no", "not", "of", "on", "or", "such", "that", "the", "their", "then", "there", "these", "they", "this", "to", "was", "will", "shall", "with"};
        List<String> wordsToRemoveList = Arrays.asList(wordsToRemove);

        String[] charsToRemove = {"_", "?", "|", "~", "!", "@", "#", "$", "₹", "%", "'", "*", "\"", "\\", "{", "}", "[", "]", "(", ")"};
        for(String character: charsToRemove)
            text = text.replace(character, " ");

        //while (text.contains("  "))
        //    text = text.trim().replace("  ", " ");
        //Removing multiple spaces, tabs, newlline chars
        text = text.trim().replaceAll("\\s+", " ");

        String[] indexes = text.split("[\\s,]+", 51);
        if(creatBurstSplits && text.length()>3){
            for(String i: indexes){
                if(!i.isEmpty() && !wordsToRemoveList.contains(i)){
                    index.add(i);
                    if(i.length()>3)
                        for(int l=3; l<i.length(); l++){
                            index.add(i.substring(0, l));
                        }
                }
            }
        }else{
            index.addAll(Arrays.asList(indexes));
        }

        //Remove repition from final index list.
        ArrayList<String> uniqueArrayList = new ArrayList<String>();
        for(int i = 0; i < index.size(); i++){
            if(!uniqueArrayList.contains(index.get(i))){ // If the value isn't in the list already
                uniqueArrayList.add(index.get(i));
            }
        }
        //Log.i(TAG, "Final Index is: " + uniqueArrayList);
        return uniqueArrayList;
    }

}
