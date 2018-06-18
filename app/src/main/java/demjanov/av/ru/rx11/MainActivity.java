package demjanov.av.ru.rx11;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    //-----Constants begin-------------------------------
    final static String TAG_OBSERVER = "OBSERVER";
    //-----Constants end---------------------------------


    //-----Booleans begin--------------------------------
    private boolean isObservable = true;
//    private boolean isCompleted = false;
    //-----Booleans end----------------------------------


    //-----Elements begin--------------------------------
    TextView textView;
    EditText editText;
    //-----Elements end----------------------------------


    //-----Observables variable begin--------------------
    Observable myObservable;
    Observer myObserver;
    Subscription mySubscription;
    //-----Observables variable end----------------------


    //-----Other variables end---------------------------
    private List<String> stringList = new ArrayList<String>();
    //-----Other variables begin-------------------------


    /////////////////////////////////////////////////////
    // Method onCreate
    ////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.textView);
        initializeEditText();

        myObservable = createObservable();
        myObserver = createObserver();
        mySubscription = myObservable.subscribe(myObserver);

    }


    /////////////////////////////////////////////////////
    // Method onDestroy
    ////////////////////////////////////////////////////
    @Override
    protected void onDestroy() {
        isObservable = false;

        super.onDestroy();
    }

    /////////////////////////////////////////////////////
    // Method initializeEditText
    ////////////////////////////////////////////////////
    private void initializeEditText(){
        editText = (EditText)findViewById(R.id.textEdit);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {      //FIXME --Add logic!!!
                stringList.add(String.valueOf(s));
            }
        });
    }


    /////////////////////////////////////////////////////
    // Method createObservable
    ////////////////////////////////////////////////////
    private Observable createObservable(){
        Observable.OnSubscribe<String> onSubscribe = new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                int i = 0;

                while (isObservable && !subscriber.isUnsubscribed()) {
                    if(i < stringList.size()) {
                        if (subscriber.isUnsubscribed()) {
                            return;
                        }
                        subscriber.onNext(stringList.get(i));
                        i++;

                        if(i > 200){
                            recreateList();
                            i = 0;
                        }
                    }
                }

                if(subscriber.isUnsubscribed()){
                    return;
                }
                subscriber.onCompleted();
            }
        };

        return Observable.create(onSubscribe)               //FIXME --fiend alternative for create
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }



    /////////////////////////////////////////////////////
    // Method createObserver
    ////////////////////////////////////////////////////
    private Observer createObserver(){
//        isCompleted = false;

        return new Observer<String>() {
            @Override
            public void onCompleted() {
                Log.d(TAG_OBSERVER, "onCompleted");
//                isCompleted = true;
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG_OBSERVER, "onError: " + e.getMessage());
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG_OBSERVER, "onNext: " + s);
                updateText(s);
            }

        };
    }


    /////////////////////////////////////////////////////
    // Method updateText
    ////////////////////////////////////////////////////
    private void updateText(String s) {
        textView.setText(s);
    }



    /////////////////////////////////////////////////////
    // Method for recreateList
    ////////////////////////////////////////////////////
    private void recreateList() {
        stringList = new ArrayList<String>();
    }



}
