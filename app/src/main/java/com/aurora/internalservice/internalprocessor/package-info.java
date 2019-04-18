/**
 * This package contains classes related to internal processing of files. Possible processing steps include
 * text extraction, translation,...
 *
 * <p>
 *     <strong>NOTE:</strong> For now, only text extraction is supported.
 * </p>
 *
 * <p>
 *     The package contains an {@link com.aurora.internalservice.internalprocessor.InternalTextProcessor} that acts
 *     as a delegator. It looks at the filetype of the file that needs to be processed, and will then decided to
 *     which specific TextExtractor the file needs to be sent.
 * </p>
 */
package com.aurora.internalservice.internalprocessor;