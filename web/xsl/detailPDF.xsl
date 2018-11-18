<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    <xsl:param name="date" select="'19-11-2018'"/>
    <xsl:param name="domainName" select="'Emoon'"/>
    <xsl:param name="categoryName" select="'Luyện Thi'"/>

    <xsl:template match="/">
        <xsl:call-template name="PDFTemplate"/>
    </xsl:template>
    <xsl:template name="PDFTemplate">
    <fo:root >
        <fo:layout-master-set>
            <fo:simple-page-master master-name="A4" page-height="8.5in" page-width="11in"
                                   margin-top="0.5in" margin-bottom="0.5in" margin-left="1in" margin-right="1in">
                <fo:region-body margin-top="0.5in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="0.75in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        <fo:page-sequence master-reference="A4">
            <fo:static-content flow-name="xsl-region-before">
                <fo:block/>
            </fo:static-content>
            <fo:static-content flow-name="xsl-region-after">
                <fo:block font-size="18pt" font-family="Arial" line-height="24pt"
                          space-after.optimum="15pt" text-align="center" padding-top="3pt">
                    <fo:page-number/>
                </fo:block>
            </fo:static-content>
            <fo:flow flow-name="xsl-region-body" font-family="Arial">
                <fo:table font-size="10pt" width="100%" table-layout="fixed"
                          border-collapse="separate"
                >
                    <fo:table-column column-width="proportional-column-width(20)"/>
                    <fo:table-column column-width="proportional-column-width(20)"/>
                    <fo:table-body>
                        <fo:table-row>
                            <fo:table-cell display-align="center" text-align="left"
                            >
                                <fo:block
                                        font-weight="bold"
                                        color="#87cefa"
                                        font-size="20pt"
                                >COURSE SOURCE
                                </fo:block>
                            </fo:table-cell>

                            <fo:table-cell display-align="center" text-align="right" >
                                <fo:block>Date: <xsl:value-of select="$date" /></fo:block>
                                </fo:table-cell>
                        </fo:table-row>
                    </fo:table-body>
                </fo:table>
                <fo:block display-align="center"

                          text-align="center"
                          font-size="28pt"
                          font-weight="bold"
                          padding-top="1cm"
                >Course detail
                </fo:block>
                <fo:block margin-top="2.0cm">
                    <fo:table border-collapse="separate" table-layout="fixed" font-family="Arial">
                        <fo:table-column column-width="5cm"/>
                        <fo:table-column column-width="15cm"/>
                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell font-weight="bold">
                                    <fo:block padding="2mm">
                                        Name:
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block padding="2mm">
                                        <xsl:value-of select="//*[local-name()='Name']" />
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell font-weight="bold">
                                    <fo:block padding="2mm">
                                        Author:
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block padding="2mm">
                                        <xsl:value-of select="//*[local-name()='Author']" />
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell font-weight="bold">
                                    <fo:block padding="2mm">
                                        Domain:
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block padding="2mm">
                                        <xsl:value-of select="$domainName"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell font-weight="bold">
                                    <fo:block padding="2mm">
                                        Category:
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block padding="2mm">
                                        <xsl:value-of select="$categoryName"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell font-weight="bold">
                                    <fo:block padding="2mm">
                                        Cost:
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block padding="2mm">
                                        <xsl:value-of select="//*[local-name()='Cost']"/>
                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                            <fo:table-row>
                                <fo:table-cell font-weight="bold">
                                    <fo:block padding="2mm">
                                        Source:
                                    </fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block padding="2mm">
                                        <fo:basic-link
                                                external-destination="url('{//*[local-name()='SourceURL']}')"
                                                color="blue" text-decoration="underline">
                                            <xsl:value-of select="//*[local-name()='SourceURL']"/>
                                        </fo:basic-link>

                                    </fo:block>
                                </fo:table-cell>
                            </fo:table-row>

                        </fo:table-body>
                    </fo:table>

                </fo:block>
                <fo:block
                        font-size="20pt"
                        text-align="center"
                        font-weight="bold"
                        margin-top="1cm"
                        padding-top="0.5cm"
                        padding-bottom="0.5cm"
                >Thank you for supporting us!
                </fo:block>
            </fo:flow>
        </fo:page-sequence>
    </fo:root>
    </xsl:template>


</xsl:stylesheet>