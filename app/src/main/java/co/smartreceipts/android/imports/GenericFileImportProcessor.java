package co.smartreceipts.android.imports;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import co.smartreceipts.android.model.Trip;
import co.smartreceipts.android.utils.UriUtils;
import co.smartreceipts.android.utils.log.Logger;
import io.reactivex.Single;
import wb.android.storage.StorageManager;

public class GenericFileImportProcessor implements FileImportProcessor {

    private final Trip trip;
    private final StorageManager storageManner;
    private final ContentResolver contentResolver;

    public GenericFileImportProcessor(@NonNull Trip trip, @NonNull StorageManager storageManager, @NonNull Context context) {
        this(trip, storageManager, context.getContentResolver());
    }

    public GenericFileImportProcessor(@NonNull Trip trip, @NonNull StorageManager storageManager, @NonNull ContentResolver contentResolver) {
        this.trip = Preconditions.checkNotNull(trip);
        this.storageManner = Preconditions.checkNotNull(storageManager);
        this.contentResolver = Preconditions.checkNotNull(contentResolver);
    }

    @NonNull
    @Override
    public Single<File> process(@NonNull final Uri uri) {
        Logger.info(GenericFileImportProcessor.this, "Attempting to import: {}", uri);
        return Single.create(emitter -> {
            InputStream inputStream = null;
            try {
                inputStream = contentResolver.openInputStream(uri);
                final File destination = storageManner.getFile(trip.getDirectory(), System.currentTimeMillis() + "." + UriUtils.getExtension(uri, contentResolver));
                if (storageManner.copy(inputStream, destination, true)) {
                    emitter.onSuccess(destination);
                    Logger.info(GenericFileImportProcessor.this, "Successfully copied Uri to the Smart Receipts directory");
                } else {
                    emitter.onError(new FileNotFoundException());
                }
            } catch (IOException e) {
                Logger.error(GenericFileImportProcessor.this, "Failed to import uri", e);
                emitter.onError(e);
            } finally {
                StorageManager.closeQuietly(inputStream);
            }
        });
    }

}
