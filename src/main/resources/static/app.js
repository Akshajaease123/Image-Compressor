const { useEffect, useMemo, useState } = React;

function formatFileSize(bytes) {
  if (!bytes && bytes !== 0) return "";
  const kb = bytes / 1024;
  if (kb < 1024) return `${kb.toFixed(1)} KB`;
  return `${(kb / 1024).toFixed(2)} MB`;
}

function App() {
  const [file, setFile] = useState(null);
  const [targetKb, setTargetKb] = useState(120);
  const [isDarkMode, setIsDarkMode] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    const savedTheme = window.localStorage.getItem("theme");
    if (savedTheme) {
      setIsDarkMode(savedTheme === "dark");
      return;
    }
    const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
    setIsDarkMode(prefersDark);
  }, []);

  useEffect(() => {
    document.body.dataset.theme = isDarkMode ? "dark" : "light";
    window.localStorage.setItem("theme", isDarkMode ? "dark" : "light");
  }, [isDarkMode]);

  const selectedFileDetails = useMemo(() => {
    if (!file) return "No file selected.";
    return `${file.name} (${formatFileSize(file.size)})`;
  }, [file]);

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");

    if (!file) {
      setError("Please choose an image file.");
      return;
    }

    if (targetKb < 10 || targetKb > 10240) {
      setError("Target size must be between 10 KB and 10240 KB.");
      return;
    }

    const formData = new FormData();
    formData.append("image", file);
    formData.append("targetKb", targetKb);

    setIsSubmitting(true);

    try {
      const response = await fetch("/compress", {
        method: "POST",
        body: formData
      });

      if (!response.ok) {
        const message = await response.text();
        throw new Error(message || "Compression failed.");
      }

      const blob = await response.blob();
      const contentDisposition = response.headers.get("content-disposition") || "";
      const match = contentDisposition.match(/filename="?([^";]+)"?/i);
      const fallback = file.name.replace(/\.[^/.]+$/, "") + "-compressed.jpg";
      const fileName = match?.[1] || fallback;

      const url = URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = fileName;
      document.body.appendChild(link);
      link.click();
      link.remove();
      URL.revokeObjectURL(url);
    } catch (submitError) {
      setError(submitError.message || "Unable to compress image.");
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main className="app-shell">
      <section className="card">
        <div className="header-row">
          <div>
            <h1>Image Compressor</h1>
            <p>Upload an image and download a compressed JPEG close to your target size.</p>
          </div>
          <button
            type="button"
            className="theme-toggle"
            onClick={() => setIsDarkMode((current) => !current)}
            aria-label="Toggle dark mode"
          >
            {isDarkMode ? "☀️ Light" : "🌙 Dark"}
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <label htmlFor="image">Image file</label>
          <input
            id="image"
            name="image"
            type="file"
            accept="image/*"
            onChange={(event) => setFile(event.target.files?.[0] || null)}
          />
          <small>{selectedFileDetails}</small>

          <label htmlFor="targetKb">Target size (KB)</label>
          <input
            id="targetKb"
            name="targetKb"
            type="number"
            min="10"
            max="10240"
            value={targetKb}
            onChange={(event) => setTargetKb(Number(event.target.value))}
          />

          {error ? <p className="error">{error}</p> : null}

          <button type="submit" className="submit-btn" disabled={isSubmitting}>
            {isSubmitting ? "Compressing..." : "Compress & Download"}
          </button>
          <small>The output format is JPEG for adjustable compression quality.</small>
        </form>
      </section>
    </main>
  );
}

ReactDOM.createRoot(document.getElementById("root")).render(<App />);
