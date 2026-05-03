import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom';
import api from '../services/api.js';
import toast from 'react-hot-toast';

export default function Dashboard() {

  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const [files, setFiles] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null);
  const [openMenuId, setOpenMenuId] = useState(null);
  const [loading, setLoading] = useState(false);
  const [search, setSearch] = useState("");
  const [folder, setFolder] = useState([]);
  const [activeFolder, setActiveFolder] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [folderName, setFolderName] = useState("");


  const totalSize = files.reduce((acc, file) => acc + file.size, 0);
  const used = (totalSize / (1024 * 1024)).toFixed(2);
  const limitMB = 1024;
  const percentageUsed = (used / limitMB) * 100;

  useEffect(() => {
    fetchFiles();
    fetchFolders();
  }, [])

  const fetchFiles = async () => {
    try {

      const response = await api.get('/files/my-files', {
        params: {
          folderId: activeFolder
        }
      });
      setFiles(response.data);

    } catch (error) {
      console.log("Error fetching files: ", error);
    }
  }

  const fetchFolders = async () => {
    try {

      const response = await api.get("/folders");
      setFolder(response.data);

    } catch (error) {
      console.log("Error fetching folders: ", error);
    }
  }

  useEffect(() => {
    fetchFiles();
  }, [activeFolder])

  const handleFileChange = async (e) => {
    setSelectedFile(e.target.files[0]);
  }

  const handleUpload = async () => {

    if (!selectedFile) {
      return toast.error("Please select a file first!");
    }

    try {

      setLoading(true);

      const formData = new FormData();
      formData.append("file", selectedFile);
      // formData.append("folderId", activeFolder);
      if (activeFolder !== null) {
        formData.append("folderId", activeFolder);
      }

      await api.post("/files/upload", formData);
      toast.success("File uploaded successfully!");

      setSelectedFile(null);
      fetchFiles();

    } catch (error) {
      console.log("Error uploading file: ", error);
      toast.error("File upload failed!");
    } finally {
      setLoading(false);
    }

  }

  const createFolder = async () => {
    // const name = prompt("Enter folder name:");

    //  Better validation
    if (!name || name.trim() === "") {
      return toast.error("Folder name is required");
    }

    try {
      await api.post("/folders", { name: name.trim() });
      toast.success("Folder created successfully!");
      fetchFolders();
    } catch (error) {
      console.log("Error creating folder: ", error);
      toast.error("Folder creation failed!");
    }
  };

  const handleDelete = async (id) => {
    try {

      await api.delete(`/files/${id}`);
      toast.success("File deleted successfully!");
      setOpenMenuId(null);
      fetchFiles();
    } catch (error) {
      console.log("Error deleting file: ", error);
      toast.error("File deletion failed!");
    }
  }

  const handleDownload = async (id) => {
    try {
      const res = await api.get(`/files/${id}/download-link`);
      setOpenMenuId(null);
      window.open(res.data, '_blank');

    } catch (error) {
      console.log("Error downloading file: ", error);
      toast.error("File download failed!");
    }
  }

  const handleDeleteFolder = async (id) => {
    try {
      await api.delete(`/folders/${id}`);
      toast.success("Folder deleted");

      setActiveFolder(null);
      fetchFolders();
      fetchFiles();

    } catch (err) {
      toast.error("Failed to delete folder");
    }
  };


  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate("/");
  }

  return (
    <div className='h-screen bg-linear-to-r from-white to-blue-300 '>

      {/* Header */}
      <div className='flex items-center justify-between px-6 py-4 bg-white shadow-md'>

        <h2 className="text-3xl font-extrabold tracking-tight">
          <span className="bg-linear-to-r from-blue-600 to-cyan-400 bg-clip-text text-transparent">
            Vaultify
          </span>
          <span className="ml-1 text-black">🔐</span>
        </h2>

        {/* Profile + Dropdown */}
        <div className='relative flex items-center gap-2'>

          <div
            onClick={() => setOpen(!open)}
            className="w-8 h-8 bg-blue-500 text-white flex items-center justify-center rounded-full cursor-pointer hover:scale-105 transition"
          >
            U
          </div>

          {open && (
            <div className="absolute right-0 top-10 w-32 bg-white shadow-lg rounded-lg p-2 border border-gray-200">
              <button
                onClick={handleLogout}
                className="w-full text-left text-md font-medium text-red-500 hover:cursor-pointer px-3 py-1.5 rounded-md transition hover:text-red-700"
              >
                Logout
              </button>
            </div>
          )}

        </div>

      </div>

      <div className="flex h-[calc(100vh-80px)] mt-1">
        <div className="w-1/3 flex flex-col border-r bg-white/60 backdrop-blur-md gap-6 p-6 
                overflow-y-auto custom-scroll max-h-full">

          <h2 className="text-xl font-semibold text-gray-800">Actions</h2>

          {/* Upload Card */}
          <div className="bg-white rounded-xl p-5 shadow-md border border-gray-200 hover:shadow-lg transition">

            <p className="mb-4 font-semibold text-gray-700">Upload File</p>

            <input
              type="file"
              onChange={handleFileChange}
              className="mb-4 block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 
      file:rounded-lg file:border-0 file:text-sm file:font-medium 
      file:bg-blue-50 file:text-blue-600 hover:file:bg-blue-100 cursor-pointer"
            />

            <button
              onClick={handleUpload}
              className="w-full bg-linear-to-r from-blue-500 to-cyan-500 text-white py-2.5 rounded-lg 
      font-medium hover:opacity-80 transition-all duration-200 shadow-sm cursor-pointer"
            >
              {loading ? "Uploading..." : "Upload File"}
            </button>

          </div>

          {/* Folder Section */}
          <div className="bg-white rounded-xl p-5 shadow-md border border-gray-200">

            <div className="flex justify-between items-center mb-3">
              <p className="font-semibold text-gray-700">Folders</p>
              <button
                onClick={() => setShowModal(true)}
                className="text-blue-500 text-lg font-bold cursor-pointer hover:text-blue-700 transition"
              >
                +
              </button>
            </div>

            {/* All Files */}
            <div
              onClick={() => setActiveFolder(null)}
              className={`p-2 rounded cursor-pointer ${activeFolder === null ? "bg-blue-100" : "hover:bg-gray-100"
                }`}
            >
              📂 All Files
            </div>

            {/* Folder List */}
            {folder.map((f) => (
              <div
                key={f.id}
                className="flex justify-between items-center p-2 rounded hover:bg-gray-100 relative"
              >
                {/* Folder Name */}
                <span
                  onClick={() => setActiveFolder(f.id)}
                  className="cursor-pointer flex-1"
                >
                  📁 {f.name}
                </span>

                {/* 3-dot button */}
                <button
                  onClick={() =>
                    setOpenMenuId(openMenuId === f.id ? null : f.id)
                  }
                  className="text-gray-500 hover:text-black px-2 py-1 rounded-full hover:bg-gray-200"
                >
                  ⋮
                </button>

                {/* Dropdown */}
                {openMenuId === f.id && (
                  <div className="absolute right-2 top-10 w-32 bg-white shadow-lg rounded-lg border z-10">

                    <button
                      onClick={() => {
                        handleDeleteFolder(f.id);
                        setOpenMenuId(null);
                      }}
                      className="w-full text-left px-3 py-2 text-sm text-red-500 hover:bg-red-50"
                    >
                      Delete
                    </button>

                    {/* Future feature */}
                    {/* 
        <button className="w-full text-left px-3 py-2 text-sm hover:bg-gray-100">
          Rename
        </button> 
        */}

                  </div>
                )}
              </div>
            ))}

          </div>

          {/* Storage Card */}
          <div className="bg-white rounded-xl p-5 shadow-md border border-gray-200 hover:shadow-lg transition">

            <p className="font-semibold text-gray-700 mb-3">Storage</p>

            <p className="text-sm text-gray-500 mb-2">
              {used} MB / {limitMB} MB
            </p>

            <div className="w-full h-2 bg-gray-200 rounded-full overflow-hidden">
              <div
                className="h-full bg-blue-500 transition-all duration-300"
                style={{ width: `${percentageUsed}%` }}
              ></div>
            </div>

          </div>



        </div>
        <div className="w-2/3 p-6 overflow-y-auto custom-scroll">
          <h2 className="text-xl font-bold mb-5 text-gray-800">My Files</h2>
          <div className="sticky top-0  z-10 pb-2">
            <input
              type="text"
              placeholder="Search files..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="mb-4 w-full px-4 py-2 border-2 border-white rounded-lg focus:ring-2 focus:ring-blue-400"
            />
          </div>

          <div className="space-y-3 min-h-[65vh] overflow-y-auto pr-2 custom-scroll">
            {files.length === 0 ? (
              <p className="text-gray-400 text-center py-6">No files yet</p>
            ) : (
              files.filter((file) => file.fileName.toLowerCase().includes(search.toLowerCase())).map((file) => (
                <div
                  key={file.id}
                  className="flex items-center justify-between bg-white rounded-2xl px-2 py-2
                     shadow-sm hover:shadow-md hover:-translate-y-0.5
                     transition-all duration-200 border border-gray-100"

                >
                  {/* LEFT SIDE */}
                  <div className="flex items-center gap-3 min-w-0">

                    {/* File Icon */}
                    <div className="w-10 h-10 flex items-center justify-center rounded-xl bg-red-50 text-red-500 font-bold">
                      PDF
                    </div>

                    {/* File Info */}
                    <div className="flex flex-col min-w-0 cursor-pointer">
                      <span className="font-sans text-gray-800 truncate max-w-87.5">
                        {file.fileName}
                      </span>

                      <span className="text-xs text-gray-400">
                        PDF Document • {new Date(file.uploadTime).toLocaleDateString()} • {Math.round(file.size / 1024)} KB
                      </span>

                    </div>
                  </div>

                  {/* RIGHT SIDE MENU */}
                  <div className="relative">
                    <button
                      onClick={() =>
                        setOpenMenuId(openMenuId === file.id ? null : file.id)
                      }
                      className="text-gray-500 hover:text-black text-xl px-3 py-2
                         rounded-full hover:bg-gray-100 transition cursor-pointer"
                    >
                      ⋮
                    </button>

                    {openMenuId === file.id && (
                      <div className="absolute right-0 mt-2 w-40 bg-white rounded-xl shadow-lg
                              overflow-hidden animate-fade-in z-10 cursor-pointer">

                        <button
                          onClick={() => handleDownload(file.id)}
                          className="w-full text-left px-4 py-2 text-sm hover:bg-blue-50 hover:text-blue-600 cursor-pointer"
                        >
                          Download
                        </button>

                        <button
                          onClick={() => handleDelete(file.id)}
                          className="w-full text-left px-4 py-2 cursor-pointer text-sm hover:bg-red-50 text-red-500"
                        >
                          Delete
                        </button>
                      </div>
                    )}
                  </div>
                </div>
              ))
            )}
          </div>
        </div>

      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black/30 flex items-center justify-center z-50">

          <div className="bg-white rounded-xl p-6 w-80 shadow-lg">
            <h3 className="text-lg font-semibold mb-4">Create Folder</h3>

            <input
              type="text"
              placeholder="Folder name..."
              value={folderName}
              onChange={(e) => setFolderName(e.target.value)}
              className="w-full px-3 py-2 border rounded-lg mb-4 focus:ring-2 focus:ring-blue-400"
            />

            <div className="flex justify-end gap-2">
              <button
                onClick={() => {
                  setShowModal(false);
                  setFolderName("");
                }}
                className="px-3 py-1.5 text-gray-500 hover:text-black"
              >
                Cancel
              </button>

              <button
                onClick={async () => {
                  if (!folderName.trim()) {
                    return toast.error("Folder name is required");
                  }

                  try {
                    await api.post("/folders", { name: folderName.trim() });
                    toast.success("Folder created!");
                    setShowModal(false);
                    setFolderName("");
                    fetchFolders();
                  } catch (err) {
                    toast.error("Failed to create folder");
                  }
                }}
                className="bg-blue-500 text-white px-4 py-1.5 rounded-lg hover:opacity-80"
              >
                Create
              </button>
            </div>
          </div>
        </div>
      )}

    </div>
  )
}