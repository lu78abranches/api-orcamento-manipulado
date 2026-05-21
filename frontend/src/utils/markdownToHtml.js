function escapeHtml(text) {
    return text
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;");
}

function isTableSeparator(line) {
    return /^\|[\s\-:|]+\|$/.test(line.trim());
}

function inlineFormat(text) {
    return escapeHtml(text).replace(/\*\*(.*?)\*\*/g, "<strong>$1</strong>");
}

export function markdownToHtml(markdown) {
    if (!markdown) {
        return "";
    }

    const rows = markdown.split("\n");
    const htmlLines = [];
    let inList = false;
    let inTable = false;
    const tableLines = [];

    const flushTable = () => {
        if (!inTable) {
            return;
        }
        inTable = false;

        const dataLines = tableLines.filter((line) => !isTableSeparator(line));
        const header = dataLines[0]?.split("|").map((cell) => cell.trim()).filter(Boolean) || [];
        const body = dataLines.slice(1).map((line) =>
            line.split("|").map((cell) => cell.trim()).filter(Boolean)
        );

        htmlLines.push("<table>");
        if (header.length) {
            htmlLines.push(
                "<thead><tr>" + header.map((cell) => `<th>${inlineFormat(cell)}</th>`).join("") + "</tr></thead>"
            );
        }
        if (body.length) {
            htmlLines.push("<tbody>");
            body.forEach((row) => {
                htmlLines.push(
                    "<tr>" + row.map((cell) => `<td>${inlineFormat(cell)}</td>`).join("") + "</tr>"
                );
            });
            htmlLines.push("</tbody>");
        }
        htmlLines.push("</table>");
        tableLines.length = 0;
    };

    rows.forEach((line) => {
        const trimmed = line.trim();

        if (trimmed === "") {
            if (inList) {
                htmlLines.push("</ul>");
                inList = false;
            }
            flushTable();
            return;
        }

        if (/^---+$/.test(trimmed)) {
            if (inList) {
                htmlLines.push("</ul>");
                inList = false;
            }
            flushTable();
            htmlLines.push("<hr />");
            return;
        }

        if (/^#{3}\s+/.test(trimmed)) {
            if (inList) {
                htmlLines.push("</ul>");
                inList = false;
            }
            flushTable();
            htmlLines.push(`<h3>${inlineFormat(trimmed.substring(4))}</h3>`);
            return;
        }

        if (/^#{2}\s+/.test(trimmed)) {
            if (inList) {
                htmlLines.push("</ul>");
                inList = false;
            }
            flushTable();
            htmlLines.push(`<h2>${inlineFormat(trimmed.substring(3))}</h2>`);
            return;
        }

        if (/^#\s+/.test(trimmed)) {
            if (inList) {
                htmlLines.push("</ul>");
                inList = false;
            }
            flushTable();
            htmlLines.push(`<h1>${inlineFormat(trimmed.substring(2))}</h1>`);
            return;
        }

        if (/^\|.*\|/.test(trimmed)) {
            if (!inTable) {
                flushTable();
                inTable = true;
            }
            tableLines.push(trimmed);
            return;
        }

        if (inTable) {
            flushTable();
        }

        if (/^-\s+/.test(trimmed)) {
            if (!inList) {
                inList = true;
                htmlLines.push("<ul>");
            }
            htmlLines.push(`<li>${inlineFormat(trimmed.substring(2))}</li>`);
            return;
        }

        htmlLines.push(`<p>${inlineFormat(trimmed)}</p>`);
    });

    if (inList) {
        htmlLines.push("</ul>");
    }
    flushTable();

    return htmlLines.join("");
}
