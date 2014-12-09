package gov.tna.discovery.taxonomy.config;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.spel.ast.Indexer;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractTaxonomyTestCase {

    public static final Logger logger = LoggerFactory.getLogger(Indexer.class);
}
