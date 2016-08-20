package co.smartreceipts.android.sync.model;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Operates as a unique identifier for synchronization data
 */
public interface Identifier extends Parcelable, Serializable, CharSequence {

    /**
     * @return - the {@link java.lang.String} representation of this id
     */
    @NonNull
    String getId();
}
