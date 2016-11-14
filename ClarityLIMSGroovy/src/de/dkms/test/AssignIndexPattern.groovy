////////////////////////////////////////////////////////////////////////
/*
 * This file is copyright 2013 GenoLogics Life Sciences Software, Inc.
 * Version: Clarity LIMS 2.0
 */
////////////////////////////////////////////////////////////////////////
import test_api.GLSRestApiUtils

/**
 * Description:
 *
 * This example shows how to use the EPP trigger points to assign reagent-labels in a specified pattern.
 */
class PatternAssigner {

    // Report status
    public static final String SUCCESS = 'OK'
    public static final String FAILURE = 'ERROR'

    // New line terminator
    public static final String LINE_TERMINATOR = '\r\n'

    // Reagent Labels
    public static final def REAGENT_MAP = [
            (27):'AD027 (ATTCCT)', (23):'AD023 (GAGTGG)', (20):'AD020 (GTGGCC)',
            (15):'AD015 (ATGTCA)'
    ]

    // Reagent Pattern
    public static final def REAGENT_PATTERN = [
            [27,27,27,27,27,15,27,27,27,27,27,27],
            [27,27,27,27,15,15,15,27,27,27,27,27],
            [27,27,27,15,15,15,15,15,20,20,20,20],
            [27,27,23,23,15,15,15,20,20,20,20,27],
            [27,23,23,23,23,15,20,20,20,20,27,27],
            [23,23,23,23,23,23,23,23,23,27,27,27],
            [27,27,27,27,27,23,23,23,27,27,27,27],
            [27,27,27,27,27,27,23,27,27,27,27,27]
    ]

    // Connection variables
    public String username
    public String password
    public String stepURI

    // Report message
    public String outputMessage

    /**
     * Sets the commonly used parameters to class variables.
     *
     * @param protocolStepURI URI of the executing protocol step
     * @param user API username
     * @param pass API password
     */
    void initialise(String protocolStepURI, String user, String pass) {
        stepURI = protocolStepURI
        username = user
        password = pass
    }

    /**
     * Returns a message to the user. Can only be done once. Used to report error or success.
     *
     * @param status code to report, either OK, ERROR or WARNING
     * @param message the message to display to the user
     */
    void report(String status, String message) {
        String reportURI = stepURI + '/programstatus'
        Node reportNode = GLSRestApiUtils.httpGET(reportURI, username, password)
        if(!reportNode.'message'[0]) {
            reportNode.'status'[0].setValue(status)
            reportNode.append(NodeBuilder.newInstance().'message'(message))
            GLSRestApiUtils.httpPUT(reportNode, reportURI, username, password)
        }
    }

    /**
     * Converts an alphabetic row / column index to its numeric index using
     * spreadsheet indexing conventions (ie: A, B .. Z, AA, .. AZ) where indexing starts at 0.
     * If the input is already an integer, it will be converted directly.
     *
     * @param letters the String containing the single dimension placement of an artifact.
     * @return an Int representing the index of the placement in the given dimension.
     */
    def convertAlphaToNumeric(String letters) {
        // If the string represents a number, translate it directly
        if(letters.isInteger()) {
            return (letters as int) - 1
        }

        // If the string represents alphabetic characters, translate them into its integer representative
        int result = 0
        letters = letters.toUpperCase()
        for (int i=0; i < letters.size(); i++)
        {
            result += (letters.charAt(i) - ('A' as char)) * (int) Math.pow(26, letters.size() - i - 1)
        }
        return result
    }

    /**
     * Split the container placement of an artifact into its x and y components.
     * These values will be offset by -1.
     *
     * @param placement a String containing the artifact placement. In the form of xx:yy.
     * @return a List containing the x index and the y index
     */
    def parsePlacement(String placement) {
        // Split the placement into its x and y components
        def splitPlacement = placement.split(':')

        // Convert each component into a numeric index
        def rowIndex = convertAlphaToNumeric(splitPlacement[0])
        def columnIndex = convertAlphaToNumeric(splitPlacement[1])

        return [rowIndex, columnIndex]
    }

    /**
     * Assign the reagent-labels in the given pattern to the samples.
     */
    void assignIndexPattern() {
        // Retrieve the reagent setup
        Node reagentSetup = GLSRestApiUtils.httpGET(stepURI + '/reagents', username, password)

        // Collect the artifact URIs and retrieve the artifacts
        def artifactURIs = reagentSetup.'output-reagents'.'output'.collect { it.@uri }.unique()
        def artifacts = GLSRestApiUtils.batchGET(artifactURIs, username, password)

        // For each artifact, determine its position and set its reagent label accordingly
        artifacts.each { artifact ->
            // Split the position into its two components
            def positionIndices = parsePlacement(artifact.'location'[0].'value'[0].text())

            // Using our relationship maps, determine which reagent should be placed at that position
            String reagentName = REAGENT_MAP[((REAGENT_PATTERN[positionIndices[0]])[positionIndices[1]])]

            // Create and attach the reagent-label node to our setup
            Node reagentNode = NodeBuilder.newInstance().'reagent-label'(name:reagentName)
            reagentSetup.'output-reagents'.'output'.find { it.@uri == GLSRestApiUtils.stripQuery(artifact.@uri) }.append(reagentNode)
        }

        // Set the reagent setup in the API
        GLSRestApiUtils.httpPOST(reagentSetup, reagentSetup.@uri, username, password)

        // Define the success message to the user
        outputMessage = "Script has completed successfully.${LINE_TERMINATOR}" +
                "Clarity LIMS reagent pattern has been applied to all containers."
    }
}

//====================================================================================================
// Script starts here.
//====================================================================================================

// Command Line
className = getClass().simpleName
def cli = new CliBuilder(usage: "groovy ${className}.groovy -i STEPURI -u USERNAME -p PASSWORD")
cli.i(argName:'stepURI',  longOpt:'stepURI',  required:true, args:1, 'Protocol Step URI (Required)')
cli.u(argName:'username', longOpt:'username', required:true, args:1, 'LIMS username (Required)')
cli.p(argName:'password', longOpt:'password', required:true, args:1, 'LIMS password (Required)')

def opt = cli.parse(args)
if (!opt) {
    System.exit(-1)
}

protocolStepURI = opt.i
username = opt.u
password = opt.p

PatternAssigner allocator = new PatternAssigner()
try {
    allocator.initialise(protocolStepURI, username, password)
    allocator.assignIndexPattern()
    allocator.report(allocator.SUCCESS, allocator.outputMessage)
} catch (Exception e) {
    allocator?.report(allocator.FAILURE, e.message)
    System.exit(-1)
}