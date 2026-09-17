const fs = require('fs');
const path = require('path');

function processFile(filePath) {
    let content = fs.readFileSync(filePath, 'utf8');
    const originalContent = content;

    // Remove imports
    content = content.replace(/^import io\.swagger\.v3\.oas\.annotations.*$\r?\n/gm, '');

    // Keywords to strip
    const keywords = ['@Tag', '@Schema', '@Parameter', '@ArraySchema', '@Content', '@Operation', '@ApiResponses', '@ApiResponse'];

    let i = 0;
    while (i < content.length) {
        // Check if we found an annotation
        let foundKw = null;
        for (const kw of keywords) {
            if (content.substring(i).startsWith(kw)) {
                foundKw = kw;
                break;
            }
        }

        if (foundKw) {
            let start = i;
            let j = i + foundKw.length;
            
            // Skip whitespaces
            while (j < content.length && /\s/.test(content[j])) j++;

            if (content[j] === '(') {
                // Balance parens
                let parenCount = 1;
                j++;
                let inString = false;
                let escape = false;

                while (j < content.length && parenCount > 0) {
                    let c = content[j];
                    if (!inString) {
                        if (c === '"') inString = true;
                        else if (c === '(') parenCount++;
                        else if (c === ')') parenCount--;
                    } else {
                        if (escape) escape = false;
                        else if (c === '\\') escape = true;
                        else if (c === '"') inString = false;
                    }
                    j++;
                }
            }
            
            // Remove the annotation (from start to j)
            // also remove trailing spaces/newlines up to the next meaningful character
            while (j < content.length && (content[j] === ' ' || content[j] === '\t' || content[j] === '\r' || content[j] === '\n')) {
                // Only consume up to 1 newline to not collapse code too much
                if (content[j] === '\n') {
                    j++;
                    break;
                }
                j++;
            }
            
            content = content.substring(0, start) + content.substring(j);
            // Don't increment i, as we just shrunk the string
        } else {
            i++;
        }
    }

    if (content !== originalContent) {
        fs.writeFileSync(filePath, content, 'utf8');
        console.log('Cleaned: ' + filePath);
    }
}

function walkDir(dir) {
    const files = fs.readdirSync(dir);
    for (const file of files) {
        const fullPath = path.join(dir, file);
        if (fs.statSync(fullPath).isDirectory()) {
            walkDir(fullPath);
        } else if (fullPath.endsWith('.java')) {
            processFile(fullPath);
        }
    }
}

walkDir('src/main/java');
