package ch.supsi.fscli.model.commands;

import ch.supsi.fscli.data_access.CommandResult;
import ch.supsi.fscli.data_access.CommandResultStatus;
import ch.supsi.fscli.model.command_management.CommandInfo;
import ch.supsi.fscli.model.command_management.ParsedCommand;
import ch.supsi.fscli.model.command_management.PathResolver;
import ch.supsi.fscli.model.inode.DirectoryINode;
import ch.supsi.fscli.model.inode.FileSystem;
import ch.supsi.fscli.model.inode.FileSystemComponent;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;


@CommandInfo(
        name = "cd",
        signatureKey = "command.cd.signature",
        descriptionKey = "command.cd.description"
)
public class CdCommand implements ICommand {

    protected FileSystem getFileSystem() {
        return FileSystem.getInstance();
    }

    @Override
    public ParsedCommand parse(List<String> args) throws ParseException {
        if (args.isEmpty()) {
            return new ParsedCommand("cd", Collections.emptyList());
        } else if (args.size() == 1) {
            return new ParsedCommand("cd", args);
        } else {
            throw new ParseException("label.cd.error", 0);
        }
    }

    @Override
    public CommandResult execute(ParsedCommand cmd) {
        FileSystem fs = getFileSystem();
        List<String> args = cmd.getArguments();
        String path = args.isEmpty() ? "/" : args.get(0);

        try {
            // 1. Directory padre del path
            DirectoryINode parent = PathResolver.resolve(path);

            // 2. Nome finale del path
            String last = PathResolver.getFileName(path);

            // caso cd ., cd /, cd a/b/
            if (last.isEmpty() || last.equals(".")) {
                fs.setCurrentWorkingDirectory(parent);
                return new CommandResult("", CommandResultStatus.SUCCESS);
            }

            // 3. Controllo esistenza
            FileSystemComponent target = parent.findEntry(last);
            if (target == null) {
                // Chiave + parametro dinamico (path)
                return new CommandResult("label.cd.notfound", CommandResultStatus.ERROR, path);
            }

            // 4. Controllo tipo
            if (!(target instanceof DirectoryINode)) {
                // Chiave + parametro dinamico (path)
                return new CommandResult("label.cd.notADirectory", CommandResultStatus.ERROR, path);
            }

            // 5. Tutto OK --> cambio directory
            fs.setCurrentWorkingDirectory((DirectoryINode) target);
            return new CommandResult("", CommandResultStatus.SUCCESS);

        } catch (IllegalArgumentException e) {
            return new CommandResult("label.cd.notfound", CommandResultStatus.ERROR, path);
        }
    }
}