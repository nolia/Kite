package org.kite.remote;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;

/**
 * TODO
 *
 * @author Nikolay Soroka
 */
public class Command implements Parcelable{

    private final int code;
    private final Bundle args;
    private final ResultReceiver resultReceiver;

    public Command(int code, Bundle args){
        this.code = code;
        this.args = args;
        this.resultReceiver = null;
    }

    public Command(int code, Bundle args, ResultReceiver resultReceiver){
        this.code = code;
        this.args = args;
        this.resultReceiver = resultReceiver;
    }

    public Command(Parcel source) {
        code = source.readInt();
        args = source.readBundle();
        resultReceiver = source.readParcelable(ClassLoader.getSystemClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeBundle(args);
        dest.writeParcelable(resultReceiver, flags);
    }

    @Override
    public String toString() {
        return "Command {" +
                "code=" + code +
                ", args=" + args +
                ", resultReceiver=" + resultReceiver +
                '}';
    }

    public static final Creator<Command> CREATOR
            = new Creator<Command>(){

        @Override
        public Command createFromParcel(Parcel source) {
            return new Command(source);
        }

        @Override
        public Command[] newArray(int size) {
            return new Command[size];
        }
    };



}
