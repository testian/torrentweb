package torrentweb;

import java.util.*;
import java.net.*;
import java.io.*;

import org.gudy.azureus2.core3.torrent.*;

public class TorrentManager {

    public enum TorrentState {

        CREATED,
        UNCREATED,
        PROGRESSING
    }
    String torrentRoot;
    static final String torrentExtension = ".torrent";
    static final String dirExtension = ".directory";
    String torrentHost;
    URL trackerURL;
    String fileRoot;
    String fileHost;
    final Map<String, WebTorrent> busyTorrents;

    public TorrentManager(String in_torrentRoot, String in_torrentHost,
            String in_trackerURL, String in_fileRoot, String in_fileHost)
            throws MalformedURLException {
        torrentRoot = in_torrentRoot;
        torrentHost = in_torrentHost;
        trackerURL = new URL(in_trackerURL);
        fileRoot = in_fileRoot;
        fileHost = in_fileHost;
        busyTorrents = new HashMap<String, WebTorrent>();
    }

    synchronized public void clean() {
        clean(torrentRoot);

    }

    private void clean(String toClean) {
        File torrentFile = new File(toClean);
        String orig = PathString.RemoveDirectoryExtension(toClean.substring(torrentRoot.length()), dirExtension);
        if (orig == null) {
            return;
        }
        if (orig.endsWith(torrentExtension)) {
            orig = orig.substring(0, orig.length() - torrentExtension.length());
        }

        File[] content = torrentFile.listFiles();
        if (content != null) {
            for (File subFile : content) {
                clean(subFile.getPath());
            }
        }

        File originalFile = new File(fileRoot + orig);
        if (!originalFile.exists()) {
            if (torrentFile.delete()) {
                System.out.println(toClean + " deleted.");
            } else {
                System.err.println("Failed to delete " + toClean + ".");

            }
        }
    }

    public WebTorrent getWebTorrent(String path) throws InvalidPathException, TOTorrentException {

        synchronized (busyTorrents) {
            WebTorrent torrent;
            torrent = busyTorrents.get(path);
            if (torrent == null) {
                return new WebTorrent(path);
            } else {
                return torrent;
            }
        }
    }

    private void assertValidOriginalPath(String path)
            throws InvalidPathException {
        if (path.length() < 2 || path.charAt(0) != File.separatorChar) {
            throw new InvalidPathException(
                    "Not allowed to create torrent for root directory");
        }
        if (path.charAt(path.length() - 1) == File.separatorChar) {
            throw new InvalidPathException("Incorrect path syntax");
        }
    }

    public String torrentName(String originalFilePath) {
        return torrentRoot + PathString.AddDirectoryExtension(originalFilePath,
                dirExtension) + torrentExtension;
    }

    public class WebTorrent {

        private String originalFilePath;
        private File originalFile;
        private File torrentFile;
        private TOTorrent torrent;
        private TOTorrentCreator torrentCreator;
        private boolean created;
        int progress;

        private WebTorrent(String originalFilePath)
                throws InvalidPathException, TOTorrentException {
            assertValidOriginalPath(originalFilePath);
            this.originalFilePath = originalFilePath;
            this.originalFile = new File(fileRoot + originalFilePath);
            torrentCreator = TOTorrentFactory.createFromFileOrDirWithComputedPieceLength(originalFile, trackerURL);
            torrentFile = new File(torrentName(originalFilePath));
            torrent = null;
            created = false;
            progress = 0;
        }

        public int getProgress() {
            if (created || isCreated()) {
                return 100;
            }
            return progress;
        }

        public TorrentState getState() {

            if (busyTorrents.containsValue(this)) {
                return TorrentState.PROGRESSING;
            }
            if (isCreated()) {
                return TorrentState.CREATED;
            }
            return TorrentState.UNCREATED;
        }

        synchronized public boolean isCreated() {

            // Is this check reliable?, torrent specific mechanisms required to
            // detect validity of the torrent-file? We may also care about
            // changing
            // tracker address etc?

            if (!torrentFile.exists()) {
                return false;
            } // Theoretically not necessery, because the check below would
            // return
            // false too if it doesn't exist.
            long origLastModified;
            if (originalFile.isDirectory()) {
                origLastModified = new DirectoryDecorator(originalFile).lastModified();
            } else {
                origLastModified = originalFile.lastModified();
            }

            created = (torrentFile.lastModified() > origLastModified); //torrent.getCreationDate()
            return created;
        }

        public String getURL() {
            return torrentHost + Conversion.VeryCompatibleQueryStringCompliant(PathString.AddDirectoryExtension(originalFilePath,
                    dirExtension)) + torrentExtension;
        }

        private void load() throws TOTorrentException //deserialise the torrent object
        {
            torrent = TOTorrentFactory.deserialiseFromBEncodedFile(torrentFile);
        }

        private void checkLoad() throws TOTorrentException //Make sure the torrent object is deserialised
        {
            if (torrent == null) {
                if (created || isCreated()) { //That check is a bit faster, but not exactly as reliable as isCreated(), but since isCreated() isn't reliable either, it doesn't matter. The idea is to execute isCreated() only once per object-lifetime.
                    load();
                } else {
                    create();
                }
            }
        }

        public boolean update() throws TOTorrentException {

            checkLoad();
            boolean rewriteNecessary = false;
            if (!torrent.getAnnounceURL().toString().equals(
                    trackerURL.toString())) {
                torrent.setAnnounceURL(trackerURL);
                rewriteNecessary = true;
            }

            /*
             * if (!fileHostUpToDate(torrent, originalFilePath)) { //Check is
             * not yet working writeFileHost(torrent, originalFilePath);
             * rewriteNecessary = true; }
             */
            if (rewriteNecessary) {
                torrent.serialiseToBEncodedFile(torrentFile);
            }
            /*
             * } catch (TOTorrentException ex){
             * defaultTOTorrentExceptionHandling(ex);
             *
             *  }
             */

            return rewriteNecessary;

        }

        // Make sure that torrent is not null? (No that isn't necessary as it's
        // a private function)
        private void writeFileHost() {

            String writePath;
            if (originalFile.isDirectory()) {

                writePath = PathString.GetParent(originalFilePath) + File.separator; // Special case for getright method
            // where the parent directory must
            // be listed instead of the actual
            // directory AND File.separator is
            // required to indicate directory
            // writePath = PathString.GetParent(originalFilePath); //Without
            // trailing file separator

            } else {
                writePath = originalFilePath;
            }

            ArrayList<String> urlList = new ArrayList<String>(); // Because
            // deluge
            // can
            // handle it

            urlList.add(fileHost + Conversion.VeryCompatibleQueryStringCompliant(writePath));
            // String urlList = fileHost +
            // Conversion.VeryCompatibleQueryStringCompliant(writePath); //It
            // doesn't work any better that way, only worse (deluge can't handle
            // it)

            torrent.setAdditionalListProperty("url-list", urlList);
        }

        public void create() throws TOTorrentException {
            //torrentFile = new File(torrentName(originalFile.getPath()));
            // try {
            torrentCreator = TOTorrentFactory.createFromFileOrDirWithComputedPieceLength(originalFile,
                    trackerURL);
            torrentCreator.addListener(new TOTorrentProgressListener() {

                public void reportCurrentTask(String arg0) {
                }

                public void reportProgress(int arg0) {
                    progress = arg0;
                }
            });
            synchronized (busyTorrents) {
                busyTorrents.put(originalFilePath, this);
            }
            try {
                torrent = torrentCreator.create();
                writeFileHost();
                torrent.serialiseToBEncodedFile(torrentFile);
            } finally {
                synchronized (busyTorrents) {
                    busyTorrents.remove(originalFilePath);
                }
            }
        }
    }
}
