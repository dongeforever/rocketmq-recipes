package org.apache.rocketmq;

import java.util.List;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Jline extends BaseCommand {

    public static void main(String[] args) throws Exception {
        new Jline().doCommandInner();
    }

    @Override public void doCommandInner() throws Exception {
        Terminal terminal = TerminalBuilder.builder()
            .color(true)
            .size(new Size(10, 10))
            .build();
        System.out.println("Type:" + terminal.getType());
        System.out.println("Height:" + terminal.getHeight());
        System.out.println("Width:" + terminal.getWidth());
        System.out.println(terminal.getClass());
        System.out.println(terminal.getSize());

        /*
            Reader reader = terminal.reader();
            while (true) {
            System.out.println("read========================================================:" +reader.read());
            Cursor cursor = terminal.getCursorPosition(x -> System.out.println(x));
            if (cursor == null) {
                System.out.println("Cannot find cursor");
            } else {
                System.out.printf("x:%d y:%d\n", cursor.getX(), cursor.getY());
            }
            terminal.puts(InfoCmp.Capability.clear_screen);

        }*/

        String[] commands = new String[10000];
        for (int i = 0; i < commands.length; i++) {
            commands[i] = "topic" + i;
        }

        Completer topicCompleter = new ArgumentCompleter(
            new StringsCompleter(commands),
            new StringsCompleter("list"));

        Completer brokerCompleter = new ArgumentCompleter(
            new StringsCompleter("broker", "cluster"),
            new StringsCompleter("list"));



        LineReader reader = LineReaderBuilder.builder()
            .terminal(terminal)
            .completer(new AggregateCompleter(topicCompleter, brokerCompleter))
            .build();
        System.out.println(reader.getClass());
        String line;
        while ((line = reader.readLine( "rocketmq > ")) != null) {
            System.out.println(line);
        }
    }

    @Override public List<String> getCmdName() {
        return null;
    }
}
