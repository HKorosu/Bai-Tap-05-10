package com.example.ormdemo.init;

import com.example.ormdemo.entity.Author;
import com.example.ormdemo.entity.AuthorProfile;
import com.example.ormdemo.entity.Book;
import com.example.ormdemo.entity.Publisher;
import com.example.ormdemo.repository.AuthorRepository;
import com.example.ormdemo.repository.BookRepository;
import com.example.ormdemo.repository.PublisherRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final BookRepository bookRepository;

    public DataInitializer(AuthorRepository authorRepository,
                           PublisherRepository publisherRepository,
                           BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        System.out.println("\n========== BẮT ĐẦU KHỞI TẠO DỮ LIỆU ==========");

        // 1. Tạo Publisher
        Publisher publisher = new Publisher("NXB Kim Đồng");
        publisherRepository.save(publisher);

        // 2. Tạo Author + AuthorProfile (quan hệ 1-1)
        Author author1 = new Author("Nguyễn Nhật Ánh");
        AuthorProfile profile1 = new AuthorProfile("Nhà văn nổi tiếng với truyện thiếu nhi");
        author1.setProfile(profile1);
        profile1.setAuthor(author1);
        authorRepository.save(author1);

        Author author2 = new Author("Tô Hoài");
        AuthorProfile profile2 = new AuthorProfile("Tác giả của Dế Mèn Phiêu Lưu Ký");
        author2.setProfile(profile2);
        profile2.setAuthor(author2);
        authorRepository.save(author2);

        // 3. Tạo Book
        Book book1 = new Book("Mắt Biếc");
        book1.setPublisher(publisher);
        book1.getAuthors().add(author1);
        bookRepository.save(book1);

        Book book2 = new Book("Dế Mèn Phiêu Lưu Ký");
        book2.setPublisher(publisher);
        book2.getAuthors().add(author2);
        bookRepository.save(book2);

        System.out.println("========== ĐÃ LƯU DỮ LIỆU MẪU ==========\n");

        // ============================================================
        // TEST 1: LAZY LOADING — Book.publisher dùng FetchType.LAZY
        // ============================================================
        System.out.println("\n========== TEST 1: LAZY LOADING (Book.publisher) ==========");
        System.out.println("-> Bước 1: Gọi findAll() để lấy danh sách Book");
        System.out.println("   (Chú ý: SQL chỉ query bảng books, KHÔNG query publishers)");
        List<Book> books = bookRepository.findAll();
        System.out.println("-> Đã load xong " + books.size() + " cuốn sách.\n");

        System.out.println("-> Bước 2: Bây giờ mới gọi book.getPublisher().getName()");
        System.out.println("   (Lúc này Hibernate mới chạy SQL query publishers nếu cần)");
        for (Book b : books) {
            System.out.println("   Sách: " + b.getTitle()
                    + " | Nhà XB: " + b.getPublisher().getName());
        }

        // ============================================================
        // TEST 2: JOIN FETCH — giải quyết N+1
        // ============================================================
        System.out.println("\n========== TEST 2: JOIN FETCH (giải quyết N+1) ==========");
        System.out.println("-> Gọi findAllWithPublisher() - chỉ 1 câu SQL duy nhất có JOIN");
        List<Book> booksWithPublisher = bookRepository.findAllWithPublisher();
        for (Book b : booksWithPublisher) {
            System.out.println("   Sách: " + b.getTitle()
                    + " | Nhà XB: " + b.getPublisher().getName());
        }

        // ============================================================
        // TEST 3: EAGER LOADING — AuthorProfile.author dùng FetchType.EAGER
        // ============================================================
        System.out.println("\n========== TEST 3: EAGER LOADING (AuthorProfile.author) ==========");
        System.out.println("-> Gọi findAll() trên AuthorProfile");
        System.out.println("   (Chú ý: SQL sẽ JOIN với bảng authors NGAY LẬP TỨC)");
        List<AuthorProfile> profiles = authorRepository.findAll().stream()
                .map(Author::getProfile)
                .toList();
        for (AuthorProfile p : profiles) {
            // Không cần gọi p.getAuthor() thì Hibernate cũng đã load Author rồi
            System.out.println("   Profile: " + p.getBiography()
                    + " | Thuộc tác giả: " + p.getAuthor().getName());
        }

        System.out.println("\n========== KẾT THÚC TEST ==========\n");
    }
}