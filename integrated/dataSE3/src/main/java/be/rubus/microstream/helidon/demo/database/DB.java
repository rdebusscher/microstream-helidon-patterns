package be.rubus.microstream.helidon.demo.database;

import be.rubus.microstream.helidon.demo.model.Book;
import be.rubus.microstream.helidon.demo.model.User;
import io.helidon.config.Config;
import io.helidon.integrations.microstream.core.EmbeddedStorageManagerBuilder;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import one.microstream.storage.types.StorageManager;

public final class DB {

    private static StorageManager INSTANCE = null;

    private DB() {

    }

    public static EmbeddedStorageManager createStorageManager(Config config) {
        // We also start the StorageManager. If that is not wanted, only when first request arrives,
        // Adapt the code so that we check if StorageManager is started and need initialisation or need when we access the root.
        EmbeddedStorageManager result = EmbeddedStorageManagerBuilder.create(config.get("one.microstream")).start();

        Root root = (Root) result.root();

        if (root == null) {
            // First time, no database yet. Create empty database.
            root = new Root();
            result.setRoot(root);
            result.storeRoot();
        }
        initData(result, root);
        INSTANCE = result;
        return result;
    }

    private static void initData(StorageManager storageManager, Root root) {
        if (root.getBooks().isEmpty()) {

            root.setStorageManager(storageManager);
            init(root, storageManager);

        } else {
            root.setStorageManager(storageManager);
        }
    }

    private static void init(Root root, StorageManager storageManager) {

        User johnDoe = new User("John Doe", "john.doe@acme.org");
        User janeDoe = new User("Jane Doe", "jane.doe@acme.org");

        root.addUser(johnDoe);
        root.addUser(janeDoe);

        addBook(root, "9780140434132", "Northanger Abbey", "Austen, Jane", 1814);
        addBook(root, "9780007148387", "War and Peace", "Tolstoy, Leo", 1865);
        addBook(root, "9780141182490", "Mrs. Dalloway", "Woolf, Virginia", 1925);
        addBook(root, "9780312243029", "The Hours", "Cunnningham, Michael", 1999);
        addBook(root, "9780141321097", "Huckleberry Finn", "Twain, Mark", 1865);
        addBook(root, "9780141439723", "Bleak House", "Dickens, Charles", 1870);
        addBook(root, "9780520235755", "The adventures of Tom Sawyer", "Twain, Mark", 1862);
        addBook(root, "9780156030410", "A Room of One's Own", "Woolf, Virginia", 1922);

        addBook(root, "9780140707342", "Hamlet, Prince of Denmark", "Shakespeare", 1603);
        addBook(root, "9780395647400", "Lord of the Rings", "Tolkien, J.R.", 1937);

        Book annaKarenina = addBook(root, "9780679783305", "Anna Karenina", "Tolstoy, Leo", 1875);
        janeDoe.addBook(annaKarenina, storageManager);

        Book book = addBook(root, "9780060114183", "One Hundred Years of Solitude", "Marquez", 1967);
        janeDoe.addBook(book, storageManager);

        Book harryPotter = addBook(root, "9780747532743", "Harry Potter", "Rowling, J.K.", 2000);
        johnDoe.addBook(harryPotter, storageManager);


    }

    private static Book addBook(Root root, String isbn, String name, String author, int year) {
        Book result = new Book(isbn, name, author, year);
        root.addBook(result);
        return result;
    }

    public static Root getRoot() {
        return (Root) INSTANCE.root();
    }
}
