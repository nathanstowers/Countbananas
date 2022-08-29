import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.xml.XmlParser

def jsonSlurper = new JsonSlurper()

File fl = new File('JSON\\FruitExport_20220601.json')

def obj = jsonSlurper.parse(fl)

if (obj.size() < 1) {
    println("Missing JSON content")
    System.exit(1)
} else {
    String getFruitOrder = obj.fruitOrder

    if(getFruitOrder == null) {
        println("No Fruit Order Present")
        System.exit(1)
    } else {
        String getOrder = obj.fruitOrder.orderBase64

        if(getOrder == null) {
            println("No Base 64 Order Present")
            System.exit(1)
        } else {

            encodedOrder = obj.fruitOrder.orderBase64

            byte[] decodedOrder = encodedOrder.decodeBase64()

            decodedOrderXML = new String(decodedOrder)

            def orderList = new XmlParser().parseText(decodedOrderXML)

            // All Fruit Records
            def fruitsToSerialize = orderList.'**'.find {it.name() == "FruitRecords" }

            def banaanBruin = fruitsToSerialize.'**'.findAll { FruitRecord -> FruitRecord.FruitType.text() == '1' && FruitRecord.FruitColor.text() == '1' }
            def banaanGeel = fruitsToSerialize.'**'.findAll { FruitRecord -> FruitRecord.FruitType.text() == '1' && FruitRecord.FruitColor.text() == '2' }
            def banaanGroen = fruitsToSerialize.'**'.findAll { FruitRecord -> FruitRecord.FruitType.text() == '1' && FruitRecord.FruitColor.text() == '3' }

            def JSONOutput = "{\"fruit\": {\"Banaan\":{\"groen\": " + banaanGroen.size().toString() + ", \"overrijp\":" + banaanBruin.size().toString() + ", \"rijp\":" + banaanGeel.size().toString() + "}}}"
            def parser = new JsonSlurper()
            def json = parser.parseText(JSONOutput)

            def finalJson = JsonOutput.toJson(json)

            Date now = new Date()
            Calendar calendar = Calendar.getInstance()
            calendar.setTime(now)

            int yearNow = calendar.get(Calendar.YEAR)
            int monthNow = calendar.get(Calendar.MONTH)
            int dayNow = calendar.get(Calendar.DAY_OF_MONTH)

            new File("FruitExport" + yearNow.toString() + monthNow.toString() + dayNow.toString() + ".json").write(finalJson)
        }
    }
}

