import React, { useState } from 'react';
import toast from 'react-hot-toast';
import api from '../services/api.js';

export default function SecurityPage({ selectedFile, onClose, onSuccess }) {

  const [accountPassword, setAccountPassword] = useState("");
  const [filePassword, setFilePassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  console.log("SecurityPage - selectedFile:", selectedFile);

  const handleEnableProtection = async () => {

    if (!accountPassword || !filePassword || !confirmPassword) {
      return toast.error("All fields required");
    }

    if (filePassword !== confirmPassword) {
      return toast.error("Passwords do not match");
    }

    if (!selectedFile || !selectedFile.id) {
      return toast.error("File information is missing");
    }

    try {
      await api.post(`/files/${selectedFile.id}/enable-protection`, {
        accountPassword,
        filePassword
      });

      toast.success("Protection enabled");

      onClose();     
      onSuccess();   

    } catch (err) {
      console.error("Protection error:", err);
      toast.error(err.response?.data || "Failed to enable protection");
    }
  };

  return (
    <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50">

      <div className="bg-white w-96 p-6 rounded-2xl shadow-2xl">

        <h2 className="text-lg font-semibold mb-4 text-center">
          🔒 Enable File Protection
        </h2>

        <div className="space-y-3">
          <input
            type="password"
            placeholder="Account Password"
            value={accountPassword}
            onChange={(e) => setAccountPassword(e.target.value)}
            className="w-full p-2 border rounded"
          />

          <input
            type="password"
            placeholder="File Password"
            value={filePassword}
            onChange={(e) => setFilePassword(e.target.value)}
            className="w-full p-2 border rounded"
          />

          <input
            type="password"
            placeholder="Confirm Password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            className="w-full p-2 border rounded"
          />
        </div>

        <div className="flex justify-between mt-5">
          <button onClick={onClose} className="text-gray-500">
            Cancel
          </button>

          <button
            onClick={handleEnableProtection}
            className="bg-blue-500 text-white px-4 py-2 rounded"
          >
            Enable
          </button>
        </div>

      </div>
    </div>
  );
}