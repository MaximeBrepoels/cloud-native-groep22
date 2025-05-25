import React, { ReactNode, useEffect } from "react";
import ReactDOM from "react-dom";

interface ModelComponentProps {
    visible: boolean;
    onClose: () => void;
    children: ReactNode;
}

const ModelComponent: React.FC<ModelComponentProps> = ({ visible, onClose, children }) => {
    useEffect(() => {
        if (visible) {
            document.body.style.overflow = "hidden";
        } else {
            document.body.style.overflow = "";
        }
        return () => {
            document.body.style.overflow = "";
        };
    }, [visible]);

    if (!visible) return null;

    return ReactDOM.createPortal(
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-40">
            <div className="bg-white rounded-lg shadow-lg p-6 max-w-md w-full relative">
                <button
                    className="absolute top-3 right-3 text-gray-400 hover:text-gray-600 text-2xl"
                    onClick={onClose}
                    aria-label="Close"
                >
                    &times;
                </button>
                {children}
            </div>
        </div>,
        typeof window !== "undefined" ? document.body : ({} as HTMLElement)
    );
};

export default ModelComponent;