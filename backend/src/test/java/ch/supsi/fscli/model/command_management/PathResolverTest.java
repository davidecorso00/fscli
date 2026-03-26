package ch.supsi.fscli.model.command_management;

import ch.supsi.fscli.model.inode.DirectoryINode;
import ch.supsi.fscli.model.inode.FileSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class PathResolverTest {

    @BeforeEach
    void setUp() throws Exception {
        Field instance = FileSystem.class.getDeclaredField("myself");
        instance.setAccessible(true);
        instance.set(null, null);

        Constructor<FileSystem> constructor = FileSystem.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        FileSystem fs = constructor.newInstance();
        instance.set(null, fs);

        DirectoryINode root = fs.getRoot();
        DirectoryINode home = new DirectoryINode(root);
        root.addEntry("home", home);
        DirectoryINode user = new DirectoryINode(home);
        home.addEntry("user", user);
        DirectoryINode docs = new DirectoryINode(user);
        user.addEntry("docs", docs);

        fs.setCurrentWorkingDirectory(user);
    }

    @Test
    void testResolveParentRelative() {
        DirectoryINode result = PathResolver.resolve("docs/file.txt");
        assertEquals("docs", getDirName(result));
    }

    @Test
    void testResolveParentAbsolute() {
        DirectoryINode result = PathResolver.resolve("/home/user");
        assertEquals("home", getDirName(result));
    }

    @Test
    void testResolveDotAndDotDot() {
        DirectoryINode result = PathResolver.resolve("../user/docs/file.txt");
        assertEquals("docs", getDirName(result));
    }

    @Test
    void testResolveThrowsOnMissingPath() {
        assertThrows(IllegalArgumentException.class, () -> PathResolver.resolve("cartellaInesistente/file"));
    }

    @Test
    void testGetFileName() {
        assertEquals("file.txt", PathResolver.getFileName("/a/b/file.txt"));
        assertEquals("dir", PathResolver.getFileName("dir"));
    }

    private String getDirName(DirectoryINode dir) {
        if (dir.getParentDirectory() == null) return "/";
        return dir.getParentDirectory().getEntries().entrySet().stream()
                .filter(e -> e.getValue() == dir)
                .map(java.util.Map.Entry::getKey)
                .findFirst()
                .orElse("?");
    }
}