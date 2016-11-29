package org.kar
import org.apache.commons.cli.Option
/**
 * Demonstrates usage of the CliBuilder with multiple arguments to create a List.
 */
class CliBuilderDemoTest   extends GroovyTestCase {
    /**
     * You can specify multiple arguments one at a time.
     */
    void testMultiOption() {
        CliBuilder cli = new CliBuilder()
        cli.with {
            a longOpt: 'arguments', args: 2, required: true, 'Two arguments'
        }
        def args = ['-a', 'arg1', '-a', 'arg2']
        def options = cli.parse(args)
 
        assert (options)
        assertEquals('First arg is available with the -a option. ', 'arg1', options.a)
        assertEquals('Should be two args, in order, available with the addition of an "s" to the option.',
                ['arg1', 'arg2'], options.as)
    }
 
    /**
     * You can specify multiple arguments together in a block, with a defined separator(in this case a comma).
     */
    void testMultiOptionWithSeparator() {
        CliBuilder cli = new CliBuilder()
        cli.with {
            a longOpt: 'arguments', args: 2, required: true, valueSeparator: ',' as char,
                    'Two arguments, separated by a comma'
        }
        def args = ['-a', 'arg1,arg2']
        def options = cli.parse(args)
 
        assert (options)
        assertEquals('First arg is available with the -a option. ', 'arg1', options.a)
        assertEquals('Should be two args, in order.', ['arg1', 'arg2'], options.as)
    }
 
    /**
     * You can also have any number of arguments by specifying UNLIMITED_VALUES.
     */
    void testUnlimitedArgs() {
//        CliBuilder cli = new CliBuilder()
        cli.with {
            a longOpt: 'arguments', args: Option.UNLIMITED_VALUES, required: true, valueSeparator: ',' as char,
                    'Two arguments, separated by a comma'
        }
        def args = ['-a', 'arg1,arg2,arg3']
        def options = cli.parse(args)
 
        assert (options)
        assertEquals('First arg is available with the -a option. ', 'arg1', options.a)
        assertEquals('Should be a list of args, in order.', ['arg1', 'arg2', 'arg3'], options.as)
 
        def args2 = ['-a', 'argOnly']
        def options2 = cli.parse(args2)
 
        assert (options)
        assertEquals('First arg is available with the -a option.', 'argOnly', options2.a)
        assertEquals('Should be a list of args, with a single entry.', ['argOnly'], options2.as)
 
//        def args3 = []
//        //this will automatically print the usage string and any validation errors to System.out
//        def options3 = cli.parse(args3)
//        assertNull(options3)
    }
}