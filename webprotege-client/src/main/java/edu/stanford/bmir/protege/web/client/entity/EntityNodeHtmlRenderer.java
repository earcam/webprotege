package edu.stanford.bmir.protege.web.client.entity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.DataResource;
import edu.stanford.bmir.protege.web.client.user.LoggedInUserProvider;
import edu.stanford.bmir.protege.web.shared.entity.EntityNode;
import edu.stanford.bmir.protege.web.shared.lang.DisplayDictionaryLanguage;
import edu.stanford.bmir.protege.web.shared.tag.Tag;
import edu.stanford.bmir.protege.web.shared.watches.Watch;
import edu.stanford.bmir.protege.web.shared.watches.WatchType;
import edu.stanford.protege.gwt.graphtree.client.TreeNodeRenderer;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.resources.WebProtegeClientBundle.BUNDLE;

/**
 * Matthew Horridge Stanford Center for Biomedical Informatics Research 28 Nov 2017
 */
public class EntityNodeHtmlRenderer implements TreeNodeRenderer<EntityNode> {

    private final LoggedInUserProvider loggedInUserProvider;

    private DisplayDictionaryLanguage language = DisplayDictionaryLanguage.empty();

    @Inject
    public EntityNodeHtmlRenderer(@Nonnull LoggedInUserProvider loggedInUserProvider) {
        this.loggedInUserProvider = checkNotNull(loggedInUserProvider);
    }

    public void setDisplayLanguage(@Nonnull DisplayDictionaryLanguage language) {
        this.language = checkNotNull(language);
    }

    @Override
    public String getHtmlRendering(EntityNode node) {
        GWT.log("[EntityNodeHtmlRenderer] Lang: " + language);
        GWT.log("[EntityNodeHtmlRenderer] Rendering node: " + node);
        StringBuilder sb = new StringBuilder();
        sb.append("<div class='wp-entity-node'>");
        String iconIri;
        DataResource icon = getIcon(node);
        sb.append("<img src='").append(icon.getSafeUri().asString()).append("'/>");
        if (node.isDeprecated()) {
            sb.append("<div class='wp-entity-node__deprecated-entity wp-entity-node__display-name'>");
        }
        else {
            sb.append("<div class='wp-entity-node__display-name'>");
        }

        if (node.getEntity().isBuiltIn()) {
            sb.append(node.getBrowserText());
        }
        else if (!language.getPrimaryLanguages().isEmpty()) {
            String text = node.getText(language.getPrimaryLanguages(), null);
            if (text == null) {
                sb.append("<span class='wp-entity-node__display-name__default-language'>");
                sb.append("&bull;&bull;&bull;&bull;&bull;");
                sb.append("</span>");

            }
            else {
                sb.append("<span class='wp-entity-node__display-name__primary-language'>");
                sb.append(text);
                sb.append("</span>");
            }
        }
        else {
            // Plain
            sb.append(node.getBrowserText());
        }
        String secondaryText = node.getText(language.getSecondaryLanguages(), null);
        if (secondaryText != null) {
            sb.append(" <span class='wp-entity-node__display-name__secondary-language'>");
            sb.append(secondaryText);
            sb.append("</span>");
        }
        else {
            // TODO:  Indicate no secondary?
        }
        sb.append("</div>");

        if (node.getOpenCommentCount() > 0) {
            sb.append("<img style='padding-left: 6px;' src='").append(BUNDLE.svgCommentSmallFilledIcon().getSafeUri().asString()).append("'/>");
            sb.append("<div style='padding-left: 4px; padding-bottom: 4px; font-size: smaller;'> (").append(node.getOpenCommentCount()).append(")</div>");
        }
        node.getWatches().stream()
            .filter(w -> loggedInUserProvider.getCurrentUserId().equals(w.getUserId()))
            .map(Watch::getType)
            .forEach(watchType -> {
                sb.append("<img style='padding-left: 4px;' src='");
                if (watchType == WatchType.ENTITY) {
                    sb.append(BUNDLE.svgEyeIcon().getSafeUri().asString());
                }
                else {
                    sb.append(BUNDLE.svgEyeIconDown().getSafeUri().asString());
                }
                sb.append("'/>");
            });
        Collection<Tag> tags = node.getTags();
        tags.forEach(tag -> {
            sb.append("<div title='")
              .append(tag.getDescription())
              .append("' class='wp-tag wp-tag--inline-tag' style='color:")
              .append(tag.getColor().getHex())
              .append("; background-color:")
              .append(tag.getBackgroundColor().getHex())
              .append(";'>");
            sb.append(tag.getLabel());
            sb.append("</div>");
        });
        sb.append("</div>");
        return sb.toString();
    }

    @Nonnull
    private DataResource getIcon(@Nonnull EntityNode node) {
        OWLEntity entity = node.getEntity();
        if (entity.isOWLClass()) {
            if (node.isDeprecated()) {
                return BUNDLE.svgDeprecatedClassIcon();
            }
            else {
                return BUNDLE.svgClassIcon();
            }
        }
        else if (entity.isOWLObjectProperty()) {
            return BUNDLE.svgObjectPropertyIcon();
        }
        else if (entity.isOWLDataProperty()) {
            return BUNDLE.svgDataPropertyIcon();
        }
        else if (entity.isOWLAnnotationProperty()) {
            return BUNDLE.svgAnnotationPropertyIcon();
        }
        else if (entity.isOWLNamedIndividual()) {
            return BUNDLE.svgIndividualIcon();
        }
        else {
            return BUNDLE.svgDatatypeIcon();
        }
    }
}
