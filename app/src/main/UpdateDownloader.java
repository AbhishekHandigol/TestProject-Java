package my.test.app

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;

import my.test.app.R;

import static android.content.Context.DOWNLOAD_SERVICE;

public class UpdateDownloader extends BroadcastReceiver {

    private long downloadID;

    public void beginDownload(final Context appContext, final String sourceUrl, final String filenameDestination) {
        appContext.registerReceiver(this,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(location))
                .setTitle("APK")
                .setDescription("Downloading")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"/" + filenameDestination)
                .setAllowedOverMetered(false)
                .setAllowedOverRoaming(false);
        request.allowScanningByMediaScanner();
        DownloadManager downloadManager = (DownloadManager) appContext.getSystemService(DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadID = downloadManager.enqueue(request);
        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            Uri uri = FileProviderUtility.getURIForDownloadId(context, this.downloadID);
            if (uri != null) {
                String mimetype = "application/vnd.android.package-archive";
                Intent installIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                installIntent.setDataAndType(uri, mimetype);
                installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(installIntent);
            } else {
                //File URI is null
            }
            context.unregisterReceiver(this);
        }
    }
}
