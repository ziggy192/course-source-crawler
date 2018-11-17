<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://www.w3.org/1999/xhtml">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    <xsl:template match="/">

        <xsl:for-each select="//*[local-name()='Course']">
            <tr>
                <td class="col1">
                    <div class="course_name">
                        <a href="course?id={*[local-name()='Id']}">
                            <xsl:value-of select="*[local-name()='Name']"/>
                        </a>

                    </div>
                    <div class="course_small_detail">

                        <img class="img_author_small"
                             src="{*[local-name()='AuthorImageURL']}"/>
                        <span class="author_name">
                            <xsl:value-of select="*[local-name()='Author']"/>
                        </span>

                    </div>


                </td>

                <td class="col2">
                    <xsl:value-of select="*[local-name()='Cost']"/>
                </td>
                <td class="col3">
                    <xsl:value-of select="*[local-name()='Rating']"/>
                </td>
            </tr>
        </xsl:for-each>
        <!--<div class="course_item">-->
        <!--<p>Id=<xsl:value-of select="*[local-name()='Id']"/>-->
        <!--</p>-->
        <!--<p>Name=<xsl:value-of select="*[local-name()='Name']"/>-->
        <!--</p>-->
        <!--<p>Author=<xsl:value-of select="*[local-name()='Author']"/>-->
        <!--</p>-->
        <!--<p>DomainId=<xsl:value-of select="*[local-name()='DomainId']"/>-->
        <!--</p>-->
        <!--<p>CategoryId=<xsl:value-of select="*[local-name()='CategoryId']"/>-->
        <!--</p>-->
        <!--<p>Rating=<xsl:value-of select="*[local-name()='Rating']"/>-->
        <!--</p>-->
        <!--<p>RatingNumber=<xsl:value-of select="*[local-name()='RatingNumber']"/>-->
        <!--</p>-->
        <!--<p>Cost=<xsl:value-of select="*[local-name()='Cost']"/>-->
        <!--</p>-->
        <!--<p>Duration=<xsl:value-of select="*[local-name()='Duration']"/>-->
        <!--</p>-->
        <!--<p>ImageURL=<xsl:value-of select="*[local-name()='ImageURL']"/>-->
        <!--</p>-->
        <!--<p>SourceURL=<xsl:value-of select="*[local-name()='SourceURL']"/>-->
        <!--</p>-->
        <!--<p>Hash=<xsl:value-of select="*[local-name()='Hash']"/>-->
        <!--</p>-->
        <!--</div>-->
    </xsl:template>

</xsl:stylesheet>